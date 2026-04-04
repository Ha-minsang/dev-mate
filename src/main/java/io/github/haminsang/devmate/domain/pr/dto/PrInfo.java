package io.github.haminsang.devmate.domain.pr.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PrInfo {

    private final String title;
    private final String url;
    private final String updatedAt;

    public static PrInfo of(String title, String url, String updatedAt) {
        return PrInfo.builder()
                .title(title)
                .url(url)
                .updatedAt(updatedAt)
                .build();
    }
}