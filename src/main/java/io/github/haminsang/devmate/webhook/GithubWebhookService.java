package io.github.haminsang.devmate.webhook;

import io.github.haminsang.devmate.domain.deploy.entity.DeployEvent;
import io.github.haminsang.devmate.domain.deploy.service.DeployEventService;
import io.github.haminsang.notification.channel.NotificationChannel;
import io.github.haminsang.notification.channel.NotificationPayload;
import io.github.haminsang.notification.channel.NotificationRequest;
import io.github.haminsang.notification.core.NotificationEngine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class GithubWebhookService {

    private final DeployEventService deployEventService;
    private final NotificationEngine notificationEngine;

    @Value("${slack.webhook.url}")
    private String slackWebhookUrl;

    public void handlePush(Map<String, Object> payload) {
        String branchName = ((String) payload.get("ref")).replace("refs/heads/", "");

        // main 브랜치 push만 처리
        if (!branchName.equals("main")) {
            return;
        }

        String repoName = (String) ((Map) payload.get("repository")).get("name");
        String pusher = (String) ((Map) payload.get("pusher")).get("name");

        List<Map> commits = (List<Map>) payload.get("commits");
        String commitMessage = commits.isEmpty() ? "" : (String) commits.get(0).get("message");
        String commitUrl = commits.isEmpty() ? "" : (String) commits.get(0).get("url");

        DeployEvent event = DeployEvent.builder()
                .repoName(repoName)
                .branchName(branchName)
                .commitMessage(commitMessage)
                .pusher(pusher)
                .commitUrl(commitUrl)
                .build();

        deployEventService.save(event);

        NotificationRequest request = NotificationRequest.builder()
                .targetId(slackWebhookUrl)
                .channel(NotificationChannel.SLACK)
                .payload(NotificationPayload.builder()
                        .title("[" + repoName + "] 새 배포 감지")
                        .body("브랜치: " + branchName + "\n배포자: " + pusher + "\n커밋: " + commitMessage)
                        .build())
                .build();

        notificationEngine.send(request);
        log.info("배포 이벤트 저장 완료: {} - {}", repoName, branchName);
    }
}