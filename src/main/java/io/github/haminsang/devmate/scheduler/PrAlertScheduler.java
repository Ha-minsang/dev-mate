package io.github.haminsang.devmate.scheduler;

import io.github.haminsang.devmate.domain.notification.service.NotificationLogService;
import io.github.haminsang.devmate.domain.pr.dto.PrInfo;
import io.github.haminsang.devmate.domain.pr.entity.PrAlertConfig;
import io.github.haminsang.devmate.domain.pr.service.PrAlertService;
import io.github.haminsang.notification.channel.NotificationChannel;
import io.github.haminsang.notification.channel.NotificationPayload;
import io.github.haminsang.notification.channel.NotificationRequest;
import io.github.haminsang.notification.core.NotificationEngine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class PrAlertScheduler {

    private final PrAlertService prAlertService;
    private final NotificationEngine notificationEngine;
    private final NotificationLogService notificationLogService;

    @Value("${slack.webhook.url}")
    private String slackWebhookUrl;

    @Scheduled(cron = "0 0 9 * * *")
    public void run() {
        log.info("PR 방치 알림 스케줄러 실행");

        List<PrAlertConfig> configs = prAlertService.getActiveConfigs();

        configs.forEach(config -> {
            List<PrInfo> stalePrs = prAlertService.getStalePrs(config);

            if (stalePrs.isEmpty()) {
                log.info("방치된 PR 없음: {}", config.getRepoName());
                return;
            }

            StringBuilder body = new StringBuilder();
            stalePrs.forEach(pr ->
                    body.append("• ").append(pr.getTitle())
                            .append("\n").append(pr.getUrl()).append("\n")
            );

            NotificationRequest request = NotificationRequest.builder()
                    .targetId(slackWebhookUrl)
                    .channel(NotificationChannel.SLACK)
                    .payload(NotificationPayload.builder()
                            .title("[" + config.getRepoName() + "] 방치된 PR " + stalePrs.size() + "개")
                            .body(body.toString())
                            .build())
                    .build();

            notificationEngine.send(request)
                    .thenAccept(result -> notificationLogService.save(request, result));
        });
    }
}