package com.airesumeanalyzer.backend.career.achievements.dto.response;

import com.airesumeanalyzer.backend.career.achievements.enums.AchievementType;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record CandidateAchievementResponse(

        UUID id,

        String achievementName,

        String issuingOrganization,

        AchievementType achievementType,

        LocalDate issueDate,

        String description,

        String achievementUrl,

        Instant createdAt,

        Instant updatedAt
) {
}
