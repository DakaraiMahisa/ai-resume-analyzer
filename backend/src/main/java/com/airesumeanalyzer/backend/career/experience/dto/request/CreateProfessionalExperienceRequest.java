package com.airesumeanalyzer.backend.career.experience.dto.request;

import com.airesumeanalyzer.backend.career.experience.enums.EmploymentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record CreateProfessionalExperienceRequest(

        @NotBlank(message = "Company name is required")
        @Size(max = 255)
        String companyName,

        @NotBlank(message = "Job title is required")
        @Size(max = 255)
        String jobTitle,

        EmploymentType employmentType,

        @Size(max = 255)
        String location,

        @NotNull(message = "Start date is required")
        LocalDate startDate,

        LocalDate endDate,

        boolean currentlyWorking,

        @Size(max = 10000)
        String description

) {}
