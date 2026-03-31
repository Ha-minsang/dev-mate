package io.github.haminsang.devmate;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@ActiveProfiles("test")
@SpringBootTest
class DevMateApplicationTests {

    @MockitoBean
    JavaMailSender javaMailSender;

	@Test
	void contextLoads() {
	}

}
