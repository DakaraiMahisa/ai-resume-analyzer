package com.airesumeanalyzer.backend.ai.validation.validator;

import com.airesumeanalyzer.backend.ai.model.claims.ClaimPriority;
import com.airesumeanalyzer.backend.ai.model.claims.ClaimType;
import com.airesumeanalyzer.backend.ai.model.claims.Evidence;
import com.airesumeanalyzer.backend.ai.model.claims.ProposedClaim;
import com.airesumeanalyzer.backend.ai.model.validation.ValidationResult;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class EvidenceValidatorTest {

    private final EvidenceValidator validator =
            new EvidenceValidator();

    @Test
    void shouldAcceptClaimWhenEvidenceExists() {

        ProposedClaim claim =
                ProposedClaim.builder()
                        .claimType(ClaimType.SKILL)
                        .extractedValue("Spring Boot")
                        .evidence(List.of(
                                Evidence.builder()
                                        .text("Spring Boot")
                                        .startOffset(10)
                                        .endOffset(21)
                                        .build()
                        ))
                        .sourceDocumentId(UUID.randomUUID())
                        .priority(ClaimPriority.REQUIRED)
                        .build();

        ValidationResult result =
                validator.validate(claim);

        assertTrue(result.valid());
        assertEquals(
                "EvidenceValidator",
                result.validator()
        );
    }

    @Test
    void shouldRejectClaimWhenEvidenceIsEmpty() {

        ProposedClaim claim =
                ProposedClaim.builder()
                        .claimType(ClaimType.SKILL)
                        .extractedValue("Kubernetes")
                        .evidence(List.of())
                        .sourceDocumentId(UUID.randomUUID())
                        .priority(ClaimPriority.PREFERRED)
                        .build();

        ValidationResult result =
                validator.validate(claim);

        assertFalse(result.valid());
        assertEquals(
                "EvidenceValidator",
                result.validator()
        );

        assertEquals(
                "No supporting evidence found.",
                result.message()
        );
    }

    @Test
    void shouldRejectClaimWhenEvidenceIsNull() {

        ProposedClaim claim =
                ProposedClaim.builder()
                        .claimType(ClaimType.SKILL)
                        .extractedValue("Kubernetes")
                        .evidence(null)
                        .sourceDocumentId(UUID.randomUUID())
                        .priority(ClaimPriority.PREFERRED)
                        .build();

        ValidationResult result =
                validator.validate(claim);

        assertFalse(result.valid());
        assertEquals(
                "No supporting evidence found.",
                result.message()
        );
    }
}