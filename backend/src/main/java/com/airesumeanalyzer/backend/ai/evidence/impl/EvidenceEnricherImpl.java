package com.airesumeanalyzer.backend.ai.evidence.impl;

import com.airesumeanalyzer.backend.ai.evidence.EvidenceEnricher;
import com.airesumeanalyzer.backend.ai.evidence.EvidenceLocator;
import com.airesumeanalyzer.backend.ai.model.claims.Evidence;
import com.airesumeanalyzer.backend.ai.model.claims.ProposedClaim;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class EvidenceEnricherImpl implements EvidenceEnricher {

    private final EvidenceLocator evidenceLocator;


    @Override
    public List<ProposedClaim> enrich(
            List<ProposedClaim> claims,
            String rawText
    ) {
        return claims.stream()
                .map(claim -> {
                    List<String> searchTerms =
                            claim.evidenceSearchTerms() != null
                                    && !claim.evidenceSearchTerms().isEmpty()
                                    ? claim.evidenceSearchTerms()
                                    : List.of(
                                    claim.originalText() != null
                                            && !claim.originalText().isBlank()
                                            ? claim.originalText()
                                            : claim.extractedValue()
                            );

                    Map<String, Evidence> uniqueEvidence =
                            new LinkedHashMap<>();

                    for (String searchTerm : searchTerms) {
                        if (searchTerm == null || searchTerm.isBlank()) {
                            continue;
                        }

                        List<Evidence> matches =
                                evidenceLocator.locate(rawText, searchTerm);

                        for (Evidence match : matches) {
                            String key = match.startOffset() + ":"
                                    + match.endOffset();

                            uniqueEvidence.putIfAbsent(key, match);
                        }
                    }

                    List<Evidence> evidence =
                            new ArrayList<>(uniqueEvidence.values());

                    log.debug(
                            "Evidence enrichment completed. "
                                    + "sourceDocumentId={}, claimType={}, "
                                    + "value={}, searchTermCount={}, evidenceCount={}",
                            claim.sourceDocumentId(),
                            claim.claimType(),
                            claim.extractedValue(),
                            searchTerms.size(),
                            evidence.size()
                    );

                    return ProposedClaim.builder()
                            .claimType(claim.claimType())
                            .extractedValue(claim.extractedValue())
                            .originalText(claim.originalText())
                            .evidenceSearchTerms(claim.evidenceSearchTerms())
                            .evidence(evidence)
                            .sourceDocumentId(claim.sourceDocumentId())
                            .priority(claim.priority())
                            .build();
                })
                .toList();
    }

}