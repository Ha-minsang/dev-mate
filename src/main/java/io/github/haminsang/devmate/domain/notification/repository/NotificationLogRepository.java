package io.github.haminsang.devmate.domain.notification.repository;

import io.github.haminsang.devmate.domain.notification.entity.NotificationChannel;
import io.github.haminsang.devmate.domain.notification.entity.NotificationLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationLogRepository extends JpaRepository<NotificationLog, Long> {

    // 채널별 최근 발송 이력 조회
    List<NotificationLog> findTop10ByChannelOrderBySentAtDesc(NotificationChannel channel);

    // 성공 여부로 조회
    List<NotificationLog> findAllBySuccess(boolean success);
}