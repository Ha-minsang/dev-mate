package io.github.haminsang.devmate.domain.deploy.controller;

import io.github.haminsang.devmate.api.common.ApiResponse;
import io.github.haminsang.devmate.domain.deploy.entity.DeployEvent;
import io.github.haminsang.devmate.domain.deploy.service.DeployEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/deploys")
public class DeployEventController {

    private final DeployEventService deployEventService;

    // 전체 최근 배포 이력 조회
    @GetMapping
    public ApiResponse<List<DeployEvent>> getAll() {
        return ApiResponse.ok(deployEventService.getRecentEvents());
    }

    // 레포별 최근 배포 이력 조회
    @GetMapping("/{repoName}")
    public ApiResponse<List<DeployEvent>> getByRepo(@PathVariable String repoName) {
        return ApiResponse.ok(deployEventService.getRecentEventsByRepo(repoName));
    }
}