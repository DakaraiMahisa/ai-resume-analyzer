package com.airesumeanalyzer.backend.career.education.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record CreateEducationRequest(

        @NotBlank(message = "Institution is required")
        @Size(max = 255)
        String institution,

        @NotBlank(message = "Degree is required")
        @Size(max = 150)
        String degree,

        @Size(max = 255)
        String fieldOfStudy,

        @Size(max = 255)
        String location,

        @NotNull(message = "Start date is required")
        LocalDate startDate,

        LocalDate endDate,

        boolean currentlyStudying,

        @Size(max = 100)
        String grade,

        @Size(max = 10000)
        String description

) {}
