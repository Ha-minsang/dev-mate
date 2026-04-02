package io.github.haminsang.devmate.domain.notification.controller;

import io.github.haminsang.devmate.api.common.ApiResponse;
import io.github.haminsang.devmate.domain.notification.entity.NotificationLog;
import io.github.haminsang.devmate.domain.notification.repository.NotificationLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
public class NotificationLogController {

    private final NotificationLogRepository notificationLogRepository;

    // 전체 알림 로그 조회 (최근 50건)
    @GetMapping
    public ApiResponse<List<NotificationLog>> getAll() {
        return ApiResponse.ok(notificationLogRepository.findTop50ByOrderBySentAtDesc());
    }

    // 실패한 알림 로그 조회
    @GetMapping("/failed")
    public ApiResponse<List<NotificationLog>> getFailed() {
        return ApiResponse.ok(notificationLogRepository.findAllBySuccess(false));
    }
}