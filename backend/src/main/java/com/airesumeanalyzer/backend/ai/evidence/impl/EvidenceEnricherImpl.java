package com.airesumeanalyzer.backend.ai.evidence.impl;

import com.airesumeanalyzer.backend.ai.evidence.EvidenceEnricher;
import com.airesumeanalyzer.backend.ai.evidence.EvidenceLocator;
import com.airesumeanalyzer.backend.ai.model.claims.Evidence;
import com.airesumeanalyzer.backend.ai.model.claims.ProposedClaim;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class EvidenceEnricherImpl implements EvidenceEnricher {

    private final EvidenceLocator evidenceLocator;

    @Override
    public List<ProposedClaim> enrich(
            List<ProposedClaim> claims,
            String rawText
    ) {

        return claims.stream()
                .map(claim -> {

                    List<Evidence> evidence =
                            evidenceLocator.locate(
                                    rawText,
                                    claim.extractedValue()
                            );

                    return ProposedClaim.builder()
                            .claimType(claim.claimType())
                            .extractedValue(claim.extractedValue())
                            .evidence(evidence)
                            .sourceDocumentId(claim.sourceDocumentId())
                            .priority(claim.priority())
                            .build();
                })
                .toList();
    }
}