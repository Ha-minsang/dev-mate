package io.github.haminsang.devmate.api.sse;

import io.github.haminsang.notification.sse.emitter.SseEmitterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/sse")
public class SseController {

    private final SseEmitterRepository sseEmitterRepository;

    // 클라이언트 SSE 연결
    @GetMapping("/connect/{targetId}")
    public SseEmitter connect(@PathVariable String targetId) {
        SseEmitter emitter = new SseEmitter(60 * 60 * 1000L); // 1시간 타임아웃

        sseEmitterRepository.save(targetId, emitter);

        emitter.onCompletion(() -> {
            sseEmitterRepository.delete(targetId);
            log.info("SSE 연결 종료: {}", targetId);
        });

        emitter.onTimeout(() -> {
            sseEmitterRepository.delete(targetId);
            log.info("SSE 연결 타임아웃: {}", targetId);
        });

        log.info("SSE 연결 수립: {}", targetId);
        return emitter;
    }
}