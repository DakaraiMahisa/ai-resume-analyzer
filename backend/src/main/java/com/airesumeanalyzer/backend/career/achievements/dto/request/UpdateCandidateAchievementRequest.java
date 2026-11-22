package com.airesumeanalyzer.backend.career.achievements.dto.request;

import com.airesumeanalyzer.backend.career.achievements.enums.AchievementType;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record UpdateCandidateAchievementRequest(

        @Size(max = 255)
        String achievementName,

        @Size(max = 255)
        String issuingOrganization,

        AchievementType achievementType,

        LocalDate issueDate,

        String description,

        @Size(max = 500)
        String achievementUrl
) {
}
