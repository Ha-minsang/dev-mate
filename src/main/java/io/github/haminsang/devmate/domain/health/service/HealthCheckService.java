package io.github.haminsang.devmate.domain.health.service;

import io.github.haminsang.devmate.domain.health.entity.HealthCheckLog;
import io.github.haminsang.devmate.domain.health.entity.ServerTarget;
import io.github.haminsang.devmate.domain.health.repository.HealthCheckLogRepository;
import io.github.haminsang.devmate.domain.health.repository.ServerTargetRepository;
import io.github.haminsang.devmate.domain.notification.service.NotificationLogService;
import io.github.haminsang.notification.channel.NotificationChannel;
import io.github.haminsang.notification.channel.NotificationPayload;
import io.github.haminsang.notification.channel.NotificationRequest;
import io.github.haminsang.notification.core.NotificationEngine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.List;

@Slf4j
@Service
public class HealthCheckService {

    private final ServerTargetRepository serverTargetRepository;
    private final HealthCheckLogRepository healthCheckLogRepository;
    private final NotificationEngine notificationEngine;
    private final NotificationLogService notificationLogService;
    private final WebClient webClient;

    @Value("${slack.webhook.url}")
    private String slackWebhookUrl;

    public HealthCheckService(ServerTargetRepository serverTargetRepository,
                              HealthCheckLogRepository healthCheckLogRepository,
                              NotificationEngine notificationEngine,
                              NotificationLogService notificationLogService,
                              @Qualifier("webClient") WebClient webClient) {
        this.serverTargetRepository = serverTargetRepository;
        this.healthCheckLogRepository = healthCheckLogRepository;
        this.notificationEngine = notificationEngine;
        this.notificationLogService = notificationLogService;
        this.webClient = webClient;
    }

    public void checkAll() {
        List<ServerTarget> targets = serverTargetRepository.findAllByActiveTrue();
        targets.forEach(this::check);
    }

    public HealthCheckLog check(ServerTarget target) {
        long startTime = System.currentTimeMillis();
        try {
            Integer statusCode = webClient.get()
                    .uri(target.getUrl())
                    .retrieve()
                    .toBodilessEntity()
                    .timeout(Duration.ofSeconds(5))
                    .map(response -> response.getStatusCode().value())
                    .block();

            long responseTime = System.currentTimeMillis() - startTime;
            boolean success = statusCode != null && statusCode < 400;

            HealthCheckLog checkLog = HealthCheckLog.builder()
                    .serverTarget(target)
                    .statusCode(statusCode)
                    .responseTime(responseTime)
                    .success(success)
                    .build();

            if (!success) {
                sendFailureAlert(target, "응답 코드: " + statusCode);
            }

            return healthCheckLogRepository.save(checkLog);

        } catch (Exception e) {
            long responseTime = System.currentTimeMillis() - startTime;
            log.error("헬스체크 실패: {} - {}", target.getUrl(), e.getMessage());

            HealthCheckLog failLog = HealthCheckLog.builder()
                    .serverTarget(target)
                    .responseTime(responseTime)
                    .success(false)
                    .errorMessage(e.getMessage())
                    .build();

            sendFailureAlert(target, e.getMessage());

            return healthCheckLogRepository.save(failLog);
        }
    }

    private void sendFailureAlert(ServerTarget target, String reason) {
        NotificationPayload payload = NotificationPayload.builder()
                .title("[DevMate] 서버 응답 실패 - " + target.getName())
                .body("URL: " + target.getUrl() + "\n원인: " + reason)
                .build();

        // 슬랙 알림
        NotificationRequest slackRequest = NotificationRequest.builder()
                .targetId(slackWebhookUrl)
                .channel(NotificationChannel.SLACK)
                .payload(payload)
                .build();
        notificationEngine.send(slackRequest)
                .thenAccept(result -> notificationLogService.save(slackRequest, result));

        // 담당자 이메일 알림
        if (target.getManagerEmail() != null && !target.getManagerEmail().isBlank()) {
            NotificationRequest emailRequest = NotificationRequest.builder()
                    .targetId(target.getManagerEmail())
                    .channel(NotificationChannel.EMAIL)
                    .payload(payload)
                    .build();
            notificationEngine.send(emailRequest)
                    .thenAccept(result -> notificationLogService.save(emailRequest, result));
        }
    }
}