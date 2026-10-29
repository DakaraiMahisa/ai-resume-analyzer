package com.airesumeanalyzer.backend.rie.matcher;

import com.airesumeanalyzer.backend.ai.domain.entity.Claim;
import com.airesumeanalyzer.backend.ai.model.claims.ClaimType;
import com.airesumeanalyzer.backend.rie.domain.RequirementExpression;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface RequirementMatcher {

    RequirementMatchingResult match(
            RequirementExpression requirement,
            Map<UUID, ClaimType> requirementClaimTypes,
            List<Claim> resumeClaims,
            EmbeddingContext embeddingContext
    );
}