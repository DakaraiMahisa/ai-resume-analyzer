package com.airesumeanalyzer.backend.ai.model.understanding;

import lombok.Builder;

import java.util.List;

@Builder
public record StructuredJobDescription(
        String jobTitle,
        String summary,
        List<String> responsibilities,
        List<Requirement> requiredSkills,
        List<Requirement> preferredSkills,
        List<Requirement> requiredQualifications,
        List<Requirement> preferredQualifications,
        List<Requirement> requiredExperience,
        List<Requirement> preferredExperience
) {

    public record Requirement(
            String value,
            Operator operator,
            List<Requirement> components,
            String originalText
    ) {
    }

    public enum Operator {
        ATOMIC,
        ALL,
        ANY
    }
}

