package com.airesumeanalyzer.backend.career.certifications.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record CreateCandidateCertificationRequest(

        @NotBlank(message = "Certification name is required")
        @Size(max = 255)
        String certificationName,

        @NotBlank(message = "Issuing organization is required")
        @Size(max = 255)
        String issuingOrganization,

        LocalDate issueDate,

        LocalDate expirationDate,

        @Size(max = 255)
        String credentialId,

        @Size(max = 500)
        String credentialUrl,

        boolean doesNotExpire
) {
}