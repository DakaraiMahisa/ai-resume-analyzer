package com.airesumeanalyzer.backend.ai.validation.validator;

import com.airesumeanalyzer.backend.ai.model.claims.ClaimPriority;
import com.airesumeanalyzer.backend.ai.model.claims.ClaimType;
import com.airesumeanalyzer.backend.ai.model.claims.ProposedClaim;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DuplicateClaimValidatorTest {

    private final DuplicateClaimValidator duplicateClaimValidator =  new DuplicateClaimValidator();

    @Test
    void shouldRemoveDuplicateClaims() {

        UUID documentId = UUID.randomUUID();

        ProposedClaim first = ProposedClaim.builder()
                .claimType(ClaimType.SKILL)
                .extractedValue("Java")
                .sourceDocumentId(documentId)
                .priority(ClaimPriority.NOT_APPLICABLE)
                .build();

        ProposedClaim duplicate = ProposedClaim.builder()
                .claimType(ClaimType.SKILL)
                .extractedValue("JAVA")
                .sourceDocumentId(documentId)
                .priority(ClaimPriority.NOT_APPLICABLE)
                .build();

        List<ProposedClaim> result =
                duplicateClaimValidator.removeDuplicates(
                        List.of(first, duplicate)
                );

        assertEquals(1, result.size());
        assertEquals("Java", result.getFirst().extractedValue());
    }

    @Test
    void shouldNotRemoveClaimsWithDifferentPriority() {

        UUID documentId = UUID.randomUUID();

        ProposedClaim required = ProposedClaim.builder()
                .claimType(ClaimType.SKILL)
                .extractedValue("Python")
                .sourceDocumentId(documentId)
                .priority(ClaimPriority.REQUIRED)
                .build();

        ProposedClaim preferred = ProposedClaim.builder()
                .claimType(ClaimType.SKILL)
                .extractedValue("Python")
                .sourceDocumentId(documentId)
                .priority(ClaimPriority.PREFERRED)
                .build();

        List<ProposedClaim> result =
                duplicateClaimValidator.removeDuplicates(
                        List.of(required, preferred)
                );

        assertEquals(2, result.size());
    }
}
