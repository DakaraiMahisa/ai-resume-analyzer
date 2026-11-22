package com.airesumeanalyzer.backend.career.experience.dto.response;

import com.airesumeanalyzer.backend.career.experience.enums.EmploymentType;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record ProfessionalExperienceResponse(

        UUID id,
        String companyName,
        String jobTitle,
        EmploymentType employmentType,
        String location,
        LocalDate startDate,
        LocalDate endDate,
        boolean currentlyWorking,
        String description,
        Instant createdAt,
        Instant updatedAt

) {}