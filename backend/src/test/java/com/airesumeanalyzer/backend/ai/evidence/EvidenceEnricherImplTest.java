package com.airesumeanalyzer.backend.ai.evidence;

import com.airesumeanalyzer.backend.ai.evidence.impl.EvidenceEnricherImpl;
import com.airesumeanalyzer.backend.ai.model.claims.ClaimPriority;
import com.airesumeanalyzer.backend.ai.model.claims.ClaimType;
import com.airesumeanalyzer.backend.ai.model.claims.Evidence;
import com.airesumeanalyzer.backend.ai.model.claims.ProposedClaim;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EvidenceEnricherImplTest {

    private final EvidenceLocator evidenceLocator =
            mock(EvidenceLocator.class);

    private final EvidenceEnricher evidenceEnricher =
            new EvidenceEnricherImpl(evidenceLocator);

    @Test
    void shouldAttachEvidenceToClaims() {

        UUID documentId = UUID.randomUUID();

        ProposedClaim claim =
                ProposedClaim.builder()
                        .claimType(ClaimType.SKILL)
                        .extractedValue("Spring Boot")
                        .sourceDocumentId(documentId)
                        .priority(ClaimPriority.REQUIRED)
                        .build();

        Evidence evidence =
                Evidence.builder()
                        .text("Spring Boot")
                        .startOffset(10)
                        .endOffset(21)
                        .build();

        when(evidenceLocator.locate(
                "Experienced with Spring Boot.",
                "Spring Boot"
        )).thenReturn(List.of(evidence));

        List<ProposedClaim> enrichedClaims =
                evidenceEnricher.enrich(
                        List.of(claim),
                        "Experienced with Spring Boot."
                );

        assertEquals(1, enrichedClaims.size());

        ProposedClaim enrichedClaim =
                enrichedClaims.get(0);

        assertEquals(
                "Spring Boot",
                enrichedClaim.extractedValue()
        );

        assertEquals(
                List.of(evidence),
                enrichedClaim.evidence()
        );

        assertEquals(
                documentId,
                enrichedClaim.sourceDocumentId()
        );

        verify(evidenceLocator)
                .locate(
                        "Experienced with Spring Boot.",
                        "Spring Boot"
                );
    }

    @Test
    void shouldAttachEmptyEvidenceWhenClaimIsNotFound() {

        UUID documentId = UUID.randomUUID();

        ProposedClaim claim =
                ProposedClaim.builder()
                        .claimType(ClaimType.SKILL)
                        .extractedValue("Kubernetes")
                        .sourceDocumentId(documentId)
                        .priority(ClaimPriority.PREFERRED)
                        .build();

        when(evidenceLocator.locate(
                "Experienced with Java and Spring Boot.",
                "Kubernetes"
        )).thenReturn(List.of());

        List<ProposedClaim> enrichedClaims =
                evidenceEnricher.enrich(
                        List.of(claim),
                        "Experienced with Java and Spring Boot."
                );

        assertEquals(1, enrichedClaims.size());

        ProposedClaim enrichedClaim =
                enrichedClaims.get(0);

        assertNotNull(enrichedClaim.evidence());

        assertTrue(enrichedClaim.evidence().isEmpty());

        assertEquals(
                "Kubernetes",
                enrichedClaim.extractedValue()
        );

        verify(evidenceLocator)
                .locate(
                        "Experienced with Java and Spring Boot.",
                        "Kubernetes"
                );
    }
}