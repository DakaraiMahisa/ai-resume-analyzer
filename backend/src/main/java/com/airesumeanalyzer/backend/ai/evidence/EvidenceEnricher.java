package com.airesumeanalyzer.backend.ai.evidence;

import com.airesumeanalyzer.backend.ai.model.claims.ProposedClaim;

import java.util.List;

public interface EvidenceEnricher {

    List<ProposedClaim> enrich(
            List<ProposedClaim> claims,
            String rawText
    );
}
