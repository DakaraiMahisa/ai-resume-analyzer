package com.airesumeanalyzer.backend.ai.validation.validator;

import com.airesumeanalyzer.backend.ai.model.claims.ProposedClaim;
import com.airesumeanalyzer.backend.ai.model.validation.ValidationResult;
import org.springframework.stereotype.Component;

@Component
public class RequiredFieldValidator implements ClaimValidator {

    @Override
    public ValidationResult validate(ProposedClaim claim) {

        if (claim == null) {
            return ValidationResult.builder()
                    .valid(false)
                    .validator("RequiredFieldValidator")
                    .message("Claim cannot be null.")
                    .build();
        }

        if (claim.claimType() == null) {
            return invalid("Claim type is required.");
        }

        if (claim.extractedValue() == null
                || claim.extractedValue().isBlank()) {
            return invalid("Extracted value is required.");
        }


        if (claim.sourceDocumentId() == null) {
            return invalid("Source document is required.");
        }

        return ValidationResult.builder()
                .valid(true)
                .validator("RequiredFieldValidator")
                .message("All required fields are present.")
                .build();
    }

    private ValidationResult invalid(String message) {

        return ValidationResult.builder()
                .valid(false)
                .validator("RequiredFieldValidator")
                .message(message)
                .build();
    }
}