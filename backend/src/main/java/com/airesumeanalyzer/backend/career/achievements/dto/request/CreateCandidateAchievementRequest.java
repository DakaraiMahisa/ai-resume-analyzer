package com.airesumeanalyzer.backend.career.achievements.dto.request;

import com.airesumeanalyzer.backend.career.achievements.enums.AchievementType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record CreateCandidateAchievementRequest(

        @NotBlank(message = "Achievement name is required")
        @Size(max = 255)
        String achievementName,

        @Size(max = 255)
        String issuingOrganization,

        @NotNull(message = "Achievement type is required")
        AchievementType achievementType,

        LocalDate issueDate,

        String description,

        @Size(max = 500)
        String achievementUrl
) {
}
