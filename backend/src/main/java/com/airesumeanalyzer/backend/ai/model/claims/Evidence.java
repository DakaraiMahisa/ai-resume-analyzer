package com.airesumeanalyzer.backend.ai.model.claims;

import lombok.Builder;

@Builder
public record Evidence(
        String text,
        int startOffset,
        int endOffset
) {
}