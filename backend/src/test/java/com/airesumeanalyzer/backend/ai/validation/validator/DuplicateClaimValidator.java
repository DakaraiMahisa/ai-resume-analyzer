package com.airesumeanalyzer.backend.ai.validation.validator;

import com.airesumeanalyzer.backend.ai.model.claims.ProposedClaim;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class DuplicateClaimValidator {

    public List<ProposedClaim> removeDuplicates(
            List<ProposedClaim> claims
    ) {

        Set<String> seen = new HashSet<>();

        return claims.stream()
                .filter(claim -> {
                    String key = buildKey(claim);

                    return seen.add(key);
                })
                .toList();
    }

    private String buildKey(ProposedClaim claim) {

        return claim.claimType()
                + "::"
                + claim.priority()
                + "::"
                + claim.extractedValue()
                .trim()
                .toLowerCase();
    }
}