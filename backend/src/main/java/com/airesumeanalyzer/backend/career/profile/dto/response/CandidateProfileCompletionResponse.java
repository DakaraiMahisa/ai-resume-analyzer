package com.airesumeanalyzer.backend.career.profile.dto.response;

import java.util.List;

public record CandidateProfileCompletionResponse(
        int completionPercentage,
        int completedFields,
        int totalFields,
        List<String> missingFields,
        boolean isComplete
) {
}