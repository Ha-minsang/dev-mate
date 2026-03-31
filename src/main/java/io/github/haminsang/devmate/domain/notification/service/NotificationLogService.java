package io.github.haminsang.devmate.domain.notification.service;

import io.github.haminsang.devmate.domain.notification.entity.NotificationChannel;
import io.github.haminsang.devmate.domain.notification.entity.NotificationLog;
import io.github.haminsang.devmate.domain.notification.repository.NotificationLogRepository;
import io.github.haminsang.notification.channel.NotificationRequest;
import io.github.haminsang.notification.channel.NotificationResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationLogService {

    private final NotificationLogRepository notificationLogRepository;

    // 알림 발송 결과 저장
    public void save(NotificationRequest request, NotificationResult result) {
        NotificationLog log = NotificationLog.builder()
                .channel(NotificationChannel.valueOf(request.getChannel().name()))
                .targetId(request.getTargetId())
                .title(request.getPayload().getTitle())
                .body(request.getPayload().getBody())
                .success(result.isSuccess())
                .errorMessage(result.getErrorMessage())
                .build();

        notificationLogRepository.save(log);
    }
}