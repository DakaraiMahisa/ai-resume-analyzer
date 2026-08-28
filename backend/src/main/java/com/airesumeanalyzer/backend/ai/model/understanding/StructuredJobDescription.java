package com.airesumeanalyzer.backend.ai.model.understanding;

import lombok.Builder;

import java.util.List;

@Builder
public record StructuredJobDescription(
        String jobTitle,
        String summary,
        List<String> responsibilities,
        List<String> requiredSkills,
        List<String> preferredSkills,
        List<String> requiredQualifications,
        List<String> preferredQualifications,
        List<String> requiredExperience,
        List<String> preferredExperience
) {
}