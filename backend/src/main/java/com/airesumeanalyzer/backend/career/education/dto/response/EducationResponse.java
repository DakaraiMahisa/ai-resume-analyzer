package com.airesumeanalyzer.backend.career.education.dto.response;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record EducationResponse(

        UUID id,
        String institution,
        String degree,
        String fieldOfStudy,
        String location,
        LocalDate startDate,
        LocalDate endDate,
        boolean currentlyStudying,
        String grade,
        String description,
        Instant createdAt,
        Instant updatedAt

) {}
