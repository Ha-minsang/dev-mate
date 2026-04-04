package io.github.haminsang.devmate.domain.notification.repository;

import io.github.haminsang.devmate.domain.notification.entity.NotificationChannel;
import io.github.haminsang.devmate.domain.notification.entity.NotificationLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificationLogRepository extends JpaRepository<NotificationLog, Long> {

    // 성공 여부로 조회
    List<NotificationLog> findAllBySuccess(boolean success);

    long countBySentAtAfter(LocalDateTime sentAt);

    List<NotificationLog> findTop50ByOrderBySentAtDesc();
}