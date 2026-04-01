package io.github.haminsang.devmate.batch;

import io.github.haminsang.devmate.domain.health.repository.HealthCheckLogRepository;
import io.github.haminsang.devmate.domain.notification.service.NotificationLogService;
import io.github.haminsang.notification.channel.NotificationChannel;
import io.github.haminsang.notification.channel.NotificationPayload;
import io.github.haminsang.notification.channel.NotificationRequest;
import io.github.haminsang.notification.core.NotificationEngine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.StepContribution;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.infrastructure.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class DailyReportTasklet implements Tasklet {

    private static final ZoneId REPORT_ZONE = ZoneId.of("Asia/Seoul");
    private static final int GITHUB_PAGE_SIZE = 100;

    private final NotificationEngine notificationEngine;
    private final NotificationLogService notificationLogService;
    private final HealthCheckLogRepository healthCheckLogRepository;
    private final WebClient webClient;

    @Value("${github.token}")
    private String githubToken;

    @Value("${github.owner}")
    private String owner;

    @Value("${github.repo}")
    private String repo;

    @Value("${slack.webhook.url}")
    private String slackWebhookUrl;

    @Value("${notification.email.from}")
    private String emailTo;

    public DailyReportTasklet(
            NotificationEngine notificationEngine,
            NotificationLogService notificationLogService,
            HealthCheckLogRepository healthCheckLogRepository,
            @Qualifier("webClient") WebClient webClient
    ) {
        this.notificationEngine = notificationEngine;
        this.notificationLogService = notificationLogService;
        this.healthCheckLogRepository = healthCheckLogRepository;
        this.webClient = webClient;
    }

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
        LocalDate reportDate = LocalDate.now(REPORT_ZONE);
        LocalDate targetDate = reportDate.minusDays(1);

        Instant fromInclusive = targetDate.atStartOfDay(REPORT_ZONE).toInstant();
        Instant toExclusive = reportDate.atStartOfDay(REPORT_ZONE).toInstant();

        log.info("일일 리포트 배치 실행 시작. 기준일={}", targetDate);

        int openCount = fetchPullRequests("open").size();
        int mergedCount = countMergedPullRequests(fromInclusive, toExclusive);

        long failCount = healthCheckLogRepository.countBySuccessFalse();

        String body = """
                열린 PR: %d개
                어제 머지된 PR: %d개
                헬스체크 실패: %d건
                """.formatted(openCount, mergedCount, failCount);

        NotificationPayload payload = NotificationPayload.builder()
                .title("[DevMate] 일일 리포트 - " + reportDate)
                .body(body)
                .build();

        NotificationRequest slackRequest = NotificationRequest.builder()
                .targetId(slackWebhookUrl)
                .channel(NotificationChannel.SLACK)
                .payload(payload)
                .build();

        NotificationRequest emailRequest = NotificationRequest.builder()
                .targetId(emailTo)
                .channel(NotificationChannel.EMAIL)
                .payload(payload)
                .build();

        sendAndSave("SLACK", slackRequest);
        sendAndSave("EMAIL", emailRequest);

        log.info("일일 리포트 배치 실행 완료. opened={}, mergedYesterday={}, failCount={}",
                openCount, mergedCount, failCount);

        return RepeatStatus.FINISHED;
    }

    private void sendAndSave(String channelName, NotificationRequest request) {
        try {
            var result = notificationEngine.send(request).join();
            notificationLogService.save(request, result);
            log.info("{} 알림 발송 및 로그 저장 완료", channelName);
        } catch (Exception e) {
            log.error("{} 알림 처리 실패", channelName, e);
            throw e;
        }
    }

    private int countMergedPullRequests(Instant fromInclusive, Instant toExclusive) {
        int count = 0;

        for (Map<String, Object> pr : fetchPullRequests("closed")) {
            Object mergedAtValue = pr.get("merged_at");

            if (!(mergedAtValue instanceof String mergedAtString) || mergedAtString.isBlank()) {
                continue;
            }

            Instant mergedAt = Instant.parse(mergedAtString);
            boolean inRange = !mergedAt.isBefore(fromInclusive) && mergedAt.isBefore(toExclusive);

            if (inRange) {
                count++;
            }
        }

        return count;
    }

    private List<Map<String, Object>> fetchPullRequests(String state) {
        List<Map<String, Object>> result = new ArrayList<>();
        int page = 1;

        while (true) {
            final int currentPage = page;

            List<Map<String, Object>> pageContent = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .scheme("https")
                            .host("api.github.com")
                            .path("/repos/{owner}/{repo}/pulls")
                            .queryParam("state", state)
                            .queryParam("sort", "updated")
                            .queryParam("direction", "desc")
                            .queryParam("per_page", GITHUB_PAGE_SIZE)
                            .queryParam("page", currentPage)
                            .build(owner, repo))
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + githubToken)
                    .header(HttpHeaders.ACCEPT, "application/vnd.github+json")
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, response -> response.bodyToMono(String.class)
                            .map(body -> new IllegalStateException("GitHub API 호출 실패: " + body)))
                    .bodyToFlux(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .collectList()
                    .block();

            if (pageContent == null || pageContent.isEmpty()) {
                break;
            }

            result.addAll(pageContent);

            if (pageContent.size() < GITHUB_PAGE_SIZE) {
                break;
            }

            page++;
        }

        return result;
    }
}