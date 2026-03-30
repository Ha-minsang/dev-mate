package io.github.haminsang.devmate.domain.health.controller;

import io.github.haminsang.devmate.api.common.ApiResponse;
import io.github.haminsang.devmate.domain.health.dto.ServerTargetRequest;
import io.github.haminsang.devmate.domain.health.entity.ServerTarget;
import io.github.haminsang.devmate.domain.health.repository.ServerTargetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/servers")
public class ServerTargetController {

    private final ServerTargetRepository serverTargetRepository;

    // 서버 목록 조회
    @GetMapping
    public ApiResponse<List<ServerTarget>> getAll() {
        return ApiResponse.ok(serverTargetRepository.findAll());
    }

    // 서버 등록
    @PostMapping
    public ApiResponse<ServerTarget> register(@RequestBody ServerTargetRequest request) {
        ServerTarget target = ServerTarget.create(request.getName(), request.getUrl(), request.getManagerEmail());
        return ApiResponse.ok(serverTargetRepository.save(target));
    }

    // 서버 활성화/비활성화
    @PatchMapping("/{id}/toggle")
    public ApiResponse<?> toggle(@PathVariable Long id) {
        ServerTarget target = serverTargetRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("서버를 찾을 수 없습니다."));
        target.toggleActive();
        serverTargetRepository.save(target);
        return ApiResponse.ok();
    }
}
