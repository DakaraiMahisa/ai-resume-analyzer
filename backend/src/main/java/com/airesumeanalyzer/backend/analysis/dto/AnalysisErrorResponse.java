package com.airesumeanalyzer.backend.analysis.dto;

import com.airesumeanalyzer.backend.analysis.enums.AnalysisFailureCode;

public record AnalysisErrorResponse(
        AnalysisFailureCode code,
        String message,
        boolean retryable
) {
}
