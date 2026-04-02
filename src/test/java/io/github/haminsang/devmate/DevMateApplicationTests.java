package io.github.haminsang.devmate;

import io.github.haminsang.notification.sse.emitter.SseEmitterRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
@ActiveProfiles("test")
class DevMateApplicationTests {

    @MockitoBean
    JavaMailSender javaMailSender;

    @MockitoBean
    SseEmitterRepository sseEmitterRepository;

    @Test
    void contextLoads() {
    }
}