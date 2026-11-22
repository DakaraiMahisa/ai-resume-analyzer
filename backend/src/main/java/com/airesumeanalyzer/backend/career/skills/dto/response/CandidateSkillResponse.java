package com.airesumeanalyzer.backend.career.skills.dto.response;

import com.airesumeanalyzer.backend.career.skills.enums.ProficiencyLevel;
import com.airesumeanalyzer.backend.career.skills.enums.SkillCategory;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record CandidateSkillResponse(
        UUID id,
        String skillName,
        SkillCategory category,
        ProficiencyLevel proficiencyLevel,
        BigDecimal yearsOfExperience,
        Instant createdAt,
        Instant updatedAt
) {}
