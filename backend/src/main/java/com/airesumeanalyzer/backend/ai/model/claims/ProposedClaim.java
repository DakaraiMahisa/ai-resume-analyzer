package com.airesumeanalyzer.backend.ai.model.claims;

import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
public record ProposedClaim(

        ClaimType claimType,

        String extractedValue,

        List<Evidence> evidence,

        UUID sourceDocumentId,

        ClaimPriority priority

) {
}
