package io.github.haminsang.devmate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class DevMateApplication {
    public static void main(String[] args) {
        SpringApplication.run(DevMateApplication.class, args);
    }
}
