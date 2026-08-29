package com.airesumeanalyzer.backend.ai.claimresolution;

import com.airesumeanalyzer.backend.ai.domain.entity.Claim;
import com.airesumeanalyzer.backend.ai.domain.entity.ClaimStatus;
import com.airesumeanalyzer.backend.ai.model.claims.ClaimPriority;
import com.airesumeanalyzer.backend.ai.model.claims.ClaimType;
import com.airesumeanalyzer.backend.ai.model.claims.Evidence;
import com.airesumeanalyzer.backend.ai.model.claims.ProposedClaim;
import com.airesumeanalyzer.backend.common.entity.Document;
import com.airesumeanalyzer.backend.processing.enums.DocumentType;
import com.airesumeanalyzer.backend.resume.entity.Resume;

import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.UUID;


import static org.junit.jupiter.api.Assertions.*;

class ClaimResolutionEngineImplTest {

    private final ClaimResolutionEngine engine =
            new ClaimResolutionEngineImpl(new ObjectMapper());

    @Test
    void shouldResolveValidatedClaim() {

        UUID documentId = UUID.randomUUID();

        Evidence evidence = Evidence.builder()
                .text("Developed REST APIs using Spring Boot.")
                .startOffset(120)
                .endOffset(164)
                .build();

        ProposedClaim proposedClaim = ProposedClaim.builder()
                .claimType(ClaimType.SKILL)
                .extractedValue("  Spring   Boot  ")
                .evidence(List.of(evidence))
                .sourceDocumentId(documentId)
                .priority(ClaimPriority.NOT_APPLICABLE)
                .build();

        List<Claim> result = engine.resolve(
                List.of(proposedClaim),
                documentId,
                DocumentType.RESUME
        );

        assertEquals(1, result.size());

        Claim claim = result.getFirst();

        assertEquals(
                documentId,
                claim.getSourceDocumentId()
        );

        assertEquals(
                DocumentType.RESUME,
                claim.getSourceDocumentType()
        );
        assertEquals(ClaimType.SKILL, claim.getClaimType());
        assertEquals("  Spring   Boot  ", claim.getOriginalValue());
        assertEquals("Spring Boot", claim.getCanonicalName());
        assertEquals(
                ClaimPriority.NOT_APPLICABLE,
                claim.getPriority()
        );
        assertEquals(ClaimStatus.VALIDATED, claim.getStatus());

        assertNotNull(claim.getEvidence());
        assertTrue(claim.getEvidence().contains("Spring Boot"));
        assertTrue(claim.getEvidence().contains("120"));
        assertTrue(claim.getEvidence().contains("164"));
    }

    @Test
    void shouldNormalizeWhitespaceWhenCanonicalizing() {

        UUID documentId = UUID.randomUUID();

        ProposedClaim proposedClaim = ProposedClaim.builder()
                .claimType(ClaimType.SKILL)
                .extractedValue("   Python     Programming   ")
                .evidence(List.of())
                .sourceDocumentId(documentId)
                .priority(ClaimPriority.NOT_APPLICABLE)
                .build();

        List<Claim> result = engine.resolve(
                List.of(proposedClaim),
                documentId,
                DocumentType.RESUME
        );

        assertEquals(
                "Python Programming",
                result.getFirst().getCanonicalName()
        );

        assertEquals(
                documentId,
                result.getFirst().getSourceDocumentId()
        );

        assertEquals(
                DocumentType.RESUME,
                result.getFirst().getSourceDocumentType()
        );
    }

    @Test
    void shouldResolveMultipleClaims() {

        UUID documentId = UUID.randomUUID();

        ProposedClaim python = ProposedClaim.builder()
                .claimType(ClaimType.SKILL)
                .extractedValue("Python")
                .evidence(List.of())
                .sourceDocumentId(documentId)
                .priority(ClaimPriority.NOT_APPLICABLE)
                .build();

        ProposedClaim springBoot = ProposedClaim.builder()
                .claimType(ClaimType.SKILL)
                .extractedValue("Spring Boot")
                .evidence(List.of())
                .sourceDocumentId(documentId)
                .priority(ClaimPriority.NOT_APPLICABLE)
                .build();

        List<Claim> result = engine.resolve(
                List.of(python, springBoot),
                documentId,
                DocumentType.RESUME
        );

        assertEquals(2, result.size());

        assertEquals(
                "Python",
                result.get(0).getCanonicalName()
        );

        assertEquals(
                "Spring Boot",
                result.get(1).getCanonicalName()
        );

        assertEquals(
                documentId,
                result.get(0).getSourceDocumentId()
        );

        assertEquals(
                documentId,
                result.get(1).getSourceDocumentId()
        );

        assertEquals(
                DocumentType.RESUME,
                result.get(0).getSourceDocumentType()
        );

        assertEquals(
                DocumentType.RESUME,
                result.get(1).getSourceDocumentType()
        );
    }

}
