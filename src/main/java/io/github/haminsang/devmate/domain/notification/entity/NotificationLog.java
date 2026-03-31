package io.github.haminsang.devmate.domain.notification.entity;

import io.github.haminsang.devmate.domain.notification.entity.NotificationChannel;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "notification_log")
public class NotificationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 알림 채널 (SSE, EMAIL, SLACK)
    @Enumerated(EnumType.STRING)
    private NotificationChannel channel;

    // 수신 대상
    private String targetId;

    // 알림 제목
    private String title;

    // 알림 내용
    private String body;

    // 발송 성공 여부
    private boolean success;

    // 실패 원인
    private String errorMessage;

    private LocalDateTime sentAt;

    @Builder
    public NotificationLog(NotificationChannel channel, String targetId,
                           String title, String body, boolean success, String errorMessage) {
        this.channel = channel;
        this.targetId = targetId;
        this.title = title;
        this.body = body;
        this.success = success;
        this.errorMessage = errorMessage;
        this.sentAt = LocalDateTime.now();
    }
}