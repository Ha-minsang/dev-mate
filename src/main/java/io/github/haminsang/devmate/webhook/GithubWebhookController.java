package io.github.haminsang.devmate.webhook;

import io.github.haminsang.devmate.webhook.GithubWebhookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/webhook")
public class GithubWebhookController {

    private final GithubWebhookService githubWebhookService;

    @PostMapping("/github")
    public void receive(@RequestHeader("X-GitHub-Event") String event,
                        @RequestBody Map<String, Object> payload) {
        log.info("GitHub Webhook 수신: {}", event);
        if ("push".equals(event)) {
            githubWebhookService.handlePush(payload);
        }
    }
}