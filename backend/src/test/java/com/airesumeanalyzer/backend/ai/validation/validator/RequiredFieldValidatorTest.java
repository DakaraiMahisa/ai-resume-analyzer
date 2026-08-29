package com.airesumeanalyzer.backend.ai.validation.validator;

import com.airesumeanalyzer.backend.ai.model.claims.ClaimPriority;
import com.airesumeanalyzer.backend.ai.model.claims.ClaimType;
import com.airesumeanalyzer.backend.ai.model.claims.ProposedClaim;
import com.airesumeanalyzer.backend.ai.model.validation.ValidationResult;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class RequiredFieldValidatorTest {

    private final RequiredFieldValidator validator =
            new RequiredFieldValidator();

    @Test
    void shouldAcceptClaimWhenRequiredFieldsArePresent() {

        ProposedClaim claim =
                ProposedClaim.builder()
                        .claimType(ClaimType.SKILL)
                        .extractedValue("Spring Boot")
                        .sourceDocumentId(UUID.randomUUID())
                        .priority(ClaimPriority.REQUIRED)
                        .build();

        ValidationResult result =
                validator.validate(claim);

        assertTrue(result.valid());
        assertEquals(
                "All required fields are present.",
                result.message()
        );
    }

    @Test
    void shouldRejectClaimWhenClaimTypeIsMissing() {

        ProposedClaim claim =
                ProposedClaim.builder()
                        .extractedValue("Spring Boot")
                        .sourceDocumentId(UUID.randomUUID())
                        .build();

        ValidationResult result =
                validator.validate(claim);

        assertFalse(result.valid());
        assertEquals(
                "Claim type is required.",
                result.message()
        );
    }

    @Test
    void shouldRejectClaimWhenExtractedValueIsMissing() {

        ProposedClaim claim =
                ProposedClaim.builder()
                        .claimType(ClaimType.SKILL)
                        .sourceDocumentId(UUID.randomUUID())
                        .build();

        ValidationResult result =
                validator.validate(claim);

        assertFalse(result.valid());
        assertEquals(
                "Extracted value is required.",
                result.message()
        );
    }


    @Test
    void shouldRejectClaimWhenSourceDocumentIsMissing() {

        ProposedClaim claim =
                ProposedClaim.builder()
                        .claimType(ClaimType.SKILL)
                        .extractedValue("Spring Boot")
                        .build();

        ValidationResult result =
                validator.validate(claim);

        assertFalse(result.valid());
        assertEquals(
                "Source document is required.",
                result.message()
        );
    }

    @Test
    void shouldRejectNullClaim() {

        ValidationResult result =
                validator.validate(null);

        assertFalse(result.valid());
        assertEquals(
                "Claim cannot be null.",
                result.message()
        );
    }
}