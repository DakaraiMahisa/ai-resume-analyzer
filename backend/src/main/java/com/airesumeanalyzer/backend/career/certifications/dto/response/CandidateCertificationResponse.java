package com.airesumeanalyzer.backend.career.certifications.dto.response;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record CandidateCertificationResponse(

        UUID id,

        String certificationName,

        String issuingOrganization,

        LocalDate issueDate,

        LocalDate expirationDate,

        String credentialId,

        String credentialUrl,

        boolean doesNotExpire,

        Instant createdAt,

        Instant updatedAt
) {
}
