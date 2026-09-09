package com.airesumeanalyzer.backend.ai.validation;


import com.airesumeanalyzer.backend.ai.model.claims.ProposedClaim;
import com.airesumeanalyzer.backend.ai.model.validation.ValidationResult;
import com.airesumeanalyzer.backend.ai.validation.validator.ClaimValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ValidationEngineImpl implements ValidationEngine {

    private final List<ClaimValidator> validators;

    @Override
    public ValidationResult validate(ProposedClaim claim) {

        for (ClaimValidator validator : validators) {

            ValidationResult result =
                    validator.validate(claim);

            if (!result.valid()) {
                return result;
            }
        }

        return ValidationResult.builder()
                .valid(true)
                .validator("ValidationEngine")
                .message("Claim passed all validation stages.")
                .build();
    }

    @Override
    public List<ValidationResult> validateAll(
            List<ProposedClaim> claims
    ) {

        return claims.stream()
                .map(this::validate)
                .toList();
    }
}
