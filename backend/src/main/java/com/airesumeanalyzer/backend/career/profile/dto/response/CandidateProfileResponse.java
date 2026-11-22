package com.airesumeanalyzer.backend.career.profile.dto.response;

import java.time.Instant;
import java.util.UUID;

public record CandidateProfileResponse(

        UUID id,
        String professionalHeadline,
        String professionalSummary,
        String phone,
        String location,
        String linkedinUrl,
        String githubUrl,
        String portfolioUrl,
        String careerObjective,
        Instant createdAt,
        Instant updatedAt

) {
}
