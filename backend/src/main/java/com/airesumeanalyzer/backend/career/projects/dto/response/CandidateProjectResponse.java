package com.airesumeanalyzer.backend.career.projects.dto.response;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record CandidateProjectResponse(
        UUID id,
        String projectName,
        String role,
        String description,
        List<String> technologies,
        String projectUrl,
        LocalDate startDate,
        LocalDate endDate,
        boolean currentlyWorking,
        Instant createdAt,
        Instant updatedAt
) {}
