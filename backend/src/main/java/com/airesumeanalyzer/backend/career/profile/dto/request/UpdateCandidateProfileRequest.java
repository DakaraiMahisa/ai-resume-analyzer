package com.airesumeanalyzer.backend.career.profile.dto.request;

import jakarta.validation.constraints.Size;

public record UpdateCandidateProfileRequest(

        @Size(max = 255, message = "Professional headline must not exceed 255 characters")
        String professionalHeadline,

        @Size(max = 10000, message = "Professional summary must not exceed 10000 characters")
        String professionalSummary,

        @Size(max = 30, message = "Phone number must not exceed 30 characters")
        String phone,

        @Size(max = 255, message = "Location must not exceed 255 characters")
        String location,

        @Size(max = 500, message = "LinkedIn URL must not exceed 500 characters")
        String linkedinUrl,

        @Size(max = 500, message = "GitHub URL must not exceed 500 characters")
        String githubUrl,

        @Size(max = 500, message = "Portfolio URL must not exceed 500 characters")
        String portfolioUrl,

        @Size(max = 10000, message = "Career objective must not exceed 10000 characters")
        String careerObjective

) {
}
