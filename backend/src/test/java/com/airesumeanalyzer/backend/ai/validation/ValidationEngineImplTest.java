package com.airesumeanalyzer.backend.ai.validation;

import com.airesumeanalyzer.backend.ai.model.claims.ClaimType;
import com.airesumeanalyzer.backend.ai.model.claims.ProposedClaim;
import com.airesumeanalyzer.backend.ai.model.validation.ValidationResult;
import com.airesumeanalyzer.backend.ai.validation.validator.ClaimValidator;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ValidationEngineImplTest {

    private final ClaimValidator firstValidator =
            mock(ClaimValidator.class);

    private final ClaimValidator secondValidator =
            mock(ClaimValidator.class);

    private final ValidationEngineImpl validationEngine =
            new ValidationEngineImpl(
                    List.of(firstValidator, secondValidator)
            );

    @Test
    void shouldAcceptClaimWhenAllValidatorsPass() {

        ProposedClaim claim =
                ProposedClaim.builder()
                        .claimType(ClaimType.SKILL)
                        .extractedValue("Spring Boot")
                        .sourceDocumentId(UUID.randomUUID())
                        .build();

        when(firstValidator.validate(claim))
                .thenReturn(success("FirstValidator"));

        when(secondValidator.validate(claim))
                .thenReturn(success("SecondValidator"));

        ValidationResult result =
                validationEngine.validate(claim);

        assertTrue(result.valid());

        assertEquals(
                "ValidationEngine",
                result.validator()
        );

        assertEquals(
                "Claim passed all validation stages.",
                result.message()
        );

        verify(firstValidator).validate(claim);
        verify(secondValidator).validate(claim);
    }

    @Test
    void shouldStopWhenValidatorFails() {

        ProposedClaim claim =
                ProposedClaim.builder()
                        .claimType(ClaimType.SKILL)
                        .extractedValue("Kubernetes")
                        .sourceDocumentId(UUID.randomUUID())
                        .build();

        when(firstValidator.validate(claim))
                .thenReturn(
                        ValidationResult.builder()
                                .valid(false)
                                .validator("FirstValidator")
                                .message("Validation failed.")
                                .build()
                );

        ValidationResult result =
                validationEngine.validate(claim);

        assertFalse(result.valid());

        assertEquals(
                "FirstValidator",
                result.validator()
        );

        assertEquals(
                "Validation failed.",
                result.message()
        );

        verify(firstValidator).validate(claim);

        verify(secondValidator, never())
                .validate(claim);
    }

    @Test
    void shouldValidateAllClaims() {

        ProposedClaim firstClaim =
                ProposedClaim.builder()
                        .claimType(ClaimType.SKILL)
                        .extractedValue("Java")
                        .sourceDocumentId(UUID.randomUUID())
                        .build();

        ProposedClaim secondClaim =
                ProposedClaim.builder()
                        .claimType(ClaimType.SKILL)
                        .extractedValue("Spring Boot")
                        .sourceDocumentId(UUID.randomUUID())
                        .build();

        when(firstValidator.validate(any()))
                .thenReturn(success("FirstValidator"));

        when(secondValidator.validate(any()))
                .thenReturn(success("SecondValidator"));

        List<ValidationResult> results =
                validationEngine.validateAll(
                        List.of(firstClaim, secondClaim)
                );

        assertEquals(2, results.size());

        assertTrue(results.get(0).valid());
        assertTrue(results.get(1).valid());

        verify(firstValidator, times(2))
                .validate(any());

        verify(secondValidator, times(2))
                .validate(any());
    }

    private ValidationResult success(String validator) {

        return ValidationResult.builder()
                .valid(true)
                .validator(validator)
                .message("Validation passed.")
                .build();
    }
}