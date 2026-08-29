package com.airesumeanalyzer.backend.ai.claimresolution;

import com.airesumeanalyzer.backend.ai.domain.entity.Claim;
import com.airesumeanalyzer.backend.ai.model.claims.ProposedClaim;
import com.airesumeanalyzer.backend.processing.enums.DocumentType;

import java.util.List;
import java.util.UUID;

public interface ClaimResolutionEngine {

    List<Claim> resolve(
            List<ProposedClaim> claims,
            UUID sourceDocumentId,
            DocumentType sourceDocumentType
    );
}