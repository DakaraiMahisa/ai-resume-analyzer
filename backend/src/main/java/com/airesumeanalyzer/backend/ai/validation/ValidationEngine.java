package com.airesumeanalyzer.backend.ai.validation;

import com.airesumeanalyzer.backend.ai.model.claims.ProposedClaim;
import com.airesumeanalyzer.backend.ai.model.validation.ValidationResult;

import java.util.List;

public interface ValidationEngine {

    ValidationResult validate(ProposedClaim claim);

    List<ValidationResult> validateAll(
            List<ProposedClaim> claims
    );
}
