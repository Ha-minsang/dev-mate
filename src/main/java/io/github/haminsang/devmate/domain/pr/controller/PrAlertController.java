package io.github.haminsang.devmate.domain.pr.controller;

import io.github.haminsang.devmate.api.common.ApiResponse;
import io.github.haminsang.devmate.domain.pr.dto.PrAlertRequest;
import io.github.haminsang.devmate.domain.pr.entity.PrAlertConfig;
import io.github.haminsang.devmate.domain.pr.repository.PrAlertConfigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pr-alerts")
public class PrAlertController {

    private final PrAlertConfigRepository prAlertConfigRepository;

    // PR 알림 설정 목록 조회
    @GetMapping
    public ApiResponse<List<PrAlertConfig>> getAll() {
        return ApiResponse.ok(prAlertConfigRepository.findAll());
    }

    // PR 알림 설정 등록
    @PostMapping
    public ApiResponse<PrAlertConfig> create(@RequestBody PrAlertRequest request) {
        PrAlertConfig config = PrAlertConfig.create(request.getRepoName(), request.getStaleHours());
        return ApiResponse.ok(prAlertConfigRepository.save(config));
    }

    // PR 알림 설정 활성화/비활성화
    @PatchMapping("/{id}/toggle")
    public ApiResponse<?> toggle(@PathVariable Long id) {
        PrAlertConfig config = prAlertConfigRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("설정을 찾을 수 없습니다."));
        config.toggleActive();
        prAlertConfigRepository.save(config);
        return ApiResponse.ok();
    }

    // PR 알림 설정 삭제
    @DeleteMapping("/{id}")
    public ApiResponse<?> delete(@PathVariable Long id) {
        prAlertConfigRepository.deleteById(id);
        return ApiResponse.ok();
    }
}