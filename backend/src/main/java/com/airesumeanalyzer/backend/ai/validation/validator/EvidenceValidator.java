package com.airesumeanalyzer.backend.ai.validation.validator;

import com.airesumeanalyzer.backend.ai.model.claims.ProposedClaim;
import com.airesumeanalyzer.backend.ai.model.validation.ValidationResult;

public class EvidenceValidator implements ClaimValidator {

    @Override
    public ValidationResult validate(ProposedClaim claim) {

        if (claim.evidence() == null || claim.evidence().isEmpty()) {
            return ValidationResult.builder()
                    .valid(false)
                    .validator("EvidenceValidator")
                    .message("No supporting evidence found.")
                    .build();
        }

        return ValidationResult.builder()
                .valid(true)
                .validator("EvidenceValidator")
                .message("Supporting evidence found.")
                .build();
    }
}