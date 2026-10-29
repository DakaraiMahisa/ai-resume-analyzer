package com.airesumeanalyzer.backend.rie.classifier.dto;

import java.util.List;

public record SemanticClassificationBatchResponse(
        List<SemanticClassificationItem> classifications
) {
}
