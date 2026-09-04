package com.airesumeanalyzer.backend.rie.service;

import com.airesumeanalyzer.backend.ai.domain.entity.Claim;
import com.airesumeanalyzer.backend.ai.domain.entity.ClaimStatus;
import com.airesumeanalyzer.backend.ai.model.claims.ClaimPriority;
import com.airesumeanalyzer.backend.ai.model.claims.ClaimType;
import com.airesumeanalyzer.backend.processing.enums.DocumentType;
import com.airesumeanalyzer.backend.rie.domain.*;
import com.airesumeanalyzer.backend.rie.matcher.RequirementMatcher;
import com.airesumeanalyzer.backend.rie.requirement.RequirementDecomposer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
@ExtendWith(MockitoExtension.class)
class RequirementMatchingServiceTest {

    @Mock
    private RequirementMatcher requirementMatcher;

    @Mock
    private RequirementDecomposer requirementDecomposer;

    @Mock
    private RequirementCoverageCalculator coverageCalculator;

    private RequirementMatchingService service;

    @BeforeEach
    void setUp() {
        service = new RequirementMatchingService(
                requirementMatcher,
                requirementDecomposer,
                coverageCalculator
        );
    }

    @Test
    void shouldReturnRequirementEvaluationFromMatcherAndCoverage() {

        Claim requirement = Claim.builder()
                .id(UUID.randomUUID())
                .sourceDocumentId(UUID.randomUUID())
                .sourceDocumentType(DocumentType.JOB_DESCRIPTION)
                .claimType(ClaimType.SKILL)
                .originalValue("Python")
                .canonicalName("Python")
                .priority(ClaimPriority.REQUIRED)
                .status(ClaimStatus.VALIDATED)
                .build();

        Claim resumeClaim = Claim.builder()
                .id(UUID.randomUUID())
                .sourceDocumentId(UUID.randomUUID())
                .sourceDocumentType(DocumentType.RESUME)
                .claimType(ClaimType.SKILL)
                .originalValue("Python")
                .canonicalName("Python")
                .priority(ClaimPriority.NOT_APPLICABLE)
                .status(ClaimStatus.VALIDATED)
                .build();

        RequirementExpression expression =
                new RequirementComponent(
                        requirement.getId(),
                        "Python"
                );

        RequirementMatch expectedMatch =
                new RequirementMatch(
                        requirement.getId(),
                        "Python",
                        resumeClaim.getId(),
                        MatchRelationship.EXACT_MATCH,
                        MatchingMethod.CANONICAL
                );

        RequirementCoverage expectedCoverage =
                new RequirementCoverage(
                        1,
                        1
                );

        RequirementEvaluation expectedEvaluation =
                new RequirementEvaluation(
                        requirement.getId(),
                        expression,
                        List.of(expectedMatch),
                        expectedCoverage
                );

        List<Claim> requirements =
                List.of(requirement);

        List<Claim> resumeClaims =
                List.of(resumeClaim);

        when(requirementDecomposer.decompose(requirement))
                .thenReturn(expression);

        when(requirementMatcher.match(
                expression,
                resumeClaims
        )).thenReturn(
                List.of(expectedMatch)
        );

        when(coverageCalculator.calculate(
                expression,
                List.of(expectedMatch)
        )).thenReturn(
                expectedCoverage
        );

        List<RequirementEvaluation> result =
                service.match(
                        requirements,
                        resumeClaims
                );

        assertEquals(
                List.of(expectedEvaluation),
                result
        );

        verify(requirementDecomposer)
                .decompose(requirement);

        verify(requirementMatcher)
                .match(
                        expression,
                        resumeClaims
                );

        verify(coverageCalculator)
                .calculate(
                        expression,
                        List.of(expectedMatch)
                );
    }
}