package com.airesumeanalyzer.backend.career.certifications.dto.request;

import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record UpdateCandidateCertificationRequest(

        @Size(max = 255)
        String certificationName,

        @Size(max = 255)
        String issuingOrganization,

        LocalDate issueDate,

        LocalDate expirationDate,

        @Size(max = 255)
        String credentialId,

        @Size(max = 500)
        String credentialUrl,

        Boolean doesNotExpire
) {
}
