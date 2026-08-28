package com.airesumeanalyzer.backend.ai.model.understanding;

import lombok.Builder;

import java.util.List;

@Builder
public record StructuredResume(
        PersonalInfo personalInfo,
        String summary,
        List<String> skills,
        List<Experience> experience,
        List<Education> education,
        List<Project> projects,
        List<String> certifications
) {

    public record PersonalInfo(
            String fullName,
            String email,
            String phone,
            String location,
            String linkedin,
            String github
    ) {
    }

    public record Experience(
            String company,
            String jobTitle,
            String startDate,
            String endDate,
            String description
    ) {
    }

    public record Education(
            String institution,
            String degree,
            String fieldOfStudy,
            String startDate,
            String endDate
    ) {
    }

    public record Project(
            String name,
            String description,
            List<String> technologies
    ) {
    }
}