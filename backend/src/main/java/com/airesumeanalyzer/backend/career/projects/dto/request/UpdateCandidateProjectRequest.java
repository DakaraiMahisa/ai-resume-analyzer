package com.airesumeanalyzer.backend.career.projects.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;

public record UpdateCandidateProjectRequest(

        @Size(max = 255)
        String projectName,

        @Size(max = 150)
        String role,

        @Size(max = 10000)
        String description,

        List<@NotBlank(message = "Technology name cannot be blank")
        @Size(max = 100, message = "Technology name must not exceed 100 characters")
                String> technologies,

        @Size(max = 500)
        String projectUrl,

        LocalDate startDate,

        LocalDate endDate,

        Boolean currentlyWorking
) {}
