package com.airesumeanalyzer.backend.ai.validation.validator;

import com.airesumeanalyzer.backend.ai.model.claims.ClaimPriority;
import com.airesumeanalyzer.backend.ai.model.claims.ClaimType;
import com.airesumeanalyzer.backend.ai.model.claims.ProposedClaim;
import com.airesumeanalyzer.backend.ai.model.validation.ValidationResult;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BusinessRuleValidatorTest {

private final BusinessRuleValidator validator =  new BusinessRuleValidator();

    @Test
    void shouldAcceptValidClaim() {
        ProposedClaim claim = ProposedClaim.builder()
                .claimType(ClaimType.SKILL)
                .extractedValue("Python")
                .evidence(List.of())
                .sourceDocumentId(UUID.randomUUID())
                .priority(ClaimPriority.NOT_APPLICABLE)
                .build();

        ValidationResult result = validator.validate(claim);

        assertTrue(result.valid());
    }


    @Test
    void shouldRejectClaimWithoutClaimType() {
        ProposedClaim claim = ProposedClaim.builder()
                .extractedValue("Python")
                .evidence(List.of())
                .sourceDocumentId(UUID.randomUUID())
                .priority(ClaimPriority.NOT_APPLICABLE)
                .build();

        ValidationResult result = validator.validate(claim);

        assertFalse(result.valid());
    }

    @Test
    void shouldRejectClaimWithoutPriority() {
        ProposedClaim claim = ProposedClaim.builder()
                .claimType(ClaimType.SKILL)
                .extractedValue("Python")
                .evidence(List.of())
                .sourceDocumentId(UUID.randomUUID())
                .build();

        ValidationResult result = validator.validate(claim);

        assertFalse(result.valid());
    }
}
