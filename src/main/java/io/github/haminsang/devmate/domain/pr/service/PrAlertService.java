package io.github.haminsang.devmate.domain.pr.service;

import io.github.haminsang.devmate.domain.pr.dto.PrInfo;
import io.github.haminsang.devmate.domain.pr.entity.PrAlertConfig;
import io.github.haminsang.devmate.domain.pr.repository.PrAlertConfigRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class PrAlertService {

    private final PrAlertConfigRepository prAlertConfigRepository;
    private final WebClient webClient;

    @Value("${github.token}")
    private String githubToken;

    @Value("${github.owner}")
    private String owner;

    // 방치된 PR 목록 조회
    public List<PrInfo> getStalePrs(PrAlertConfig config) {
        List<Map> prs = webClient.get()
                .uri("https://api.github.com/repos/{owner}/{repo}/pulls?state=open",
                        owner, config.getRepoName())
                .header("Authorization", "Bearer " + githubToken)
                .header("Accept", "application/vnd.github.v3+json")
                .retrieve()
                .bodyToFlux(Map.class)
                .collectList()
                .block();

        if (prs == null) return List.of();

        LocalDateTime threshold = LocalDateTime.now().minusHours(config.getStaleHours());

        return prs.stream()
                .filter(pr -> {
                    String updatedAt = (String) pr.get("updated_at");
                    LocalDateTime updatedTime = LocalDateTime.parse(
                            updatedAt, DateTimeFormatter.ISO_DATE_TIME);
                    return updatedTime.isBefore(threshold);
                })
                .map(pr -> PrInfo.of(
                        (String) pr.get("title"),
                        (String) pr.get("html_url"),
                        (String) pr.get("updated_at")
                ))
                .toList();
    }

    // 활성화된 모든 설정에 대해 방치 PR 확인
    public List<PrAlertConfig> getActiveConfigs() {
        return prAlertConfigRepository.findAllByActiveTrue();
    }
}