package com.airesumeanalyzer.backend.ai.validation.validator;

import com.airesumeanalyzer.backend.ai.model.claims.ClaimPriority;
import com.airesumeanalyzer.backend.ai.model.claims.ClaimType;
import com.airesumeanalyzer.backend.ai.model.claims.ProposedClaim;
import com.airesumeanalyzer.backend.ai.model.validation.ValidationResult;
import org.springframework.stereotype.Component;

@Component
public class BusinessRuleValidator implements ClaimValidator {

    @Override
    public ValidationResult validate(ProposedClaim claim) {

        if (claim.claimType() == null) {
            return ValidationResult.builder()
                    .valid(false)
                    .validator("BusinessRuleValidator")
                    .message("Claim type is not supported.")
                    .build();
        }

        if (claim.priority() == null) {
            return ValidationResult.builder()
                    .valid(false)
                    .validator("BusinessRuleValidator")
                    .message("Claim priority is not defined.")
                    .build();
        }

        if (claim.claimType() == ClaimType.SKILL
                && claim.priority() == ClaimPriority.NOT_APPLICABLE) {

            return ValidationResult.builder()
                    .valid(true)
                    .validator("BusinessRuleValidator")
                    .message("Skill claim passed business rules.")
                    .build();
        }

        return ValidationResult.builder()
                .valid(true)
                .validator("BusinessRuleValidator")
                .message("Claim passed business rules.")
                .build();
    }
}