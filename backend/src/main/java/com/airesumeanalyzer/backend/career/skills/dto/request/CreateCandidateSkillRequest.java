package com.airesumeanalyzer.backend.career.skills.dto.request;

import com.airesumeanalyzer.backend.career.skills.enums.ProficiencyLevel;
import com.airesumeanalyzer.backend.career.skills.enums.SkillCategory;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record CreateCandidateSkillRequest(

        @NotBlank(message = "Skill name is required")
        @Size(max = 150, message = "Skill name must not exceed 150 characters")
        String skillName,

        @NotNull(message = "Skill category is required")
        SkillCategory category,

        @NotNull(message = "Proficiency level is required")
        ProficiencyLevel proficiencyLevel,

        @DecimalMin(value = "0.0", message = "Years of experience cannot be negative")
        @DecimalMax(value = "99.9", message = "Years of experience cannot exceed 99.9")
        @Digits(integer = 2, fraction = 1)
        BigDecimal yearsOfExperience
) {}
