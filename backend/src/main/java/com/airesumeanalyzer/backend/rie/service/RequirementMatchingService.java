package com.airesumeanalyzer.backend.rie.service;

import com.airesumeanalyzer.backend.ai.domain.entity.Claim;
import com.airesumeanalyzer.backend.ai.model.claims.ClaimType;
import com.airesumeanalyzer.backend.ai.model.understanding.StructuredJobDescription;
import com.airesumeanalyzer.backend.embedding.service.EmbeddingService;
import com.airesumeanalyzer.backend.rie.capability.SemanticMatchCandidate;
import com.airesumeanalyzer.backend.rie.domain.*;
import com.airesumeanalyzer.backend.rie.matcher.*;
import com.airesumeanalyzer.backend.rie.requirement.StructuredRequirementExpressionBuilder;
import com.airesumeanalyzer.backend.rie.requirement.RequirementClaimConsistencyValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RequirementMatchingService {

    private final RequirementMatcher requirementMatcher;
    private final StructuredRequirementExpressionBuilder expressionBuilder;
    private final RequirementCoverageCalculator coverageCalculator;
    private final EmbeddingService embeddingService;
    private final SemanticBatchClassifier semanticBatchClassifier;
    private final RequirementClaimConsistencyValidator
            requirementClaimConsistencyValidator;

    public List<RequirementMatchResult> match(
            List<RequirementToEvaluate> requirements,
            List<Claim> requirementClaims,
            List<Claim> resumeClaims
    ) {
        Objects.requireNonNull(
                requirements,
                "requirements must not be null"
        );

        Objects.requireNonNull(
                requirementClaims,
                "requirementClaims must not be null"
        );

        Objects.requireNonNull(
                resumeClaims,
                "resumeClaims must not be null"
        );

        if (requirements.isEmpty()) {
            log.info("No requirements available for matching.");
            return List.of();
        }

        log.info(
                "Validating requirement/claim consistency. requirements={}, "
                        + "validatedRequirementClaims={}, resumeClaims={}",
                requirements.size(),
                requirementClaims.size(),
                resumeClaims.size()
        );
        requirementClaimConsistencyValidator.validate(
                requirements,
                requirementClaims
        );
        Map<UUID, ClaimType> requirementClaimTypes =
                requirementClaims.stream()
                        .filter(Objects::nonNull)
                        .collect(Collectors.toMap(
                                Claim::getId,
                                Claim::getClaimType,
                                (left, right) -> left
                        ));
        log.info("Requirement/claim consistency validation passed.");

        List<PreparedRequirement> preparedRequirements =
                new ArrayList<>();

        for (RequirementToEvaluate requirementToEvaluate : requirements) {

            if (requirementToEvaluate == null) {
                continue;
            }

            StructuredJobDescription.Requirement requirement =
                    requirementToEvaluate.requirement();

            RequirementExpression expression =
                    Objects.requireNonNull(
                            expressionBuilder.build(
                                    requirement,
                                    requirementClaims
                            ),
                            "expression builder returned null expression"
                    );

            preparedRequirements.add(
                    new PreparedRequirement(
                            requirementToEvaluate,
                            expression
                    )
            );
        }
        log.info(
                "Requirement expressions built. preparedRequirements={}",
                preparedRequirements.size()
        );


        Set<String> embeddingInputs =
                new LinkedHashSet<>();

        for (PreparedRequirement preparedRequirement :
                preparedRequirements) {

            List<RequirementComponent> components =
                    RequirementExpressionComponents.extract(
                            preparedRequirement.expression()
                    );

            for (RequirementComponent component : components) {

                String value = component.value();

                if (value != null && !value.isBlank()) {
                    embeddingInputs.add(value.trim());
                }
            }
        }

        for (Claim resumeClaim : resumeClaims) {

            if (resumeClaim == null) {
                continue;
            }

            String canonicalName =
                    resumeClaim.getCanonicalName();

            if (canonicalName != null
                    && !canonicalName.isBlank()) {

                embeddingInputs.add(
                        canonicalName.trim()
                );
            }
        }

        EmbeddingContext embeddingContext =
                new EmbeddingContext(
                        embeddingService.embedBatch(
                                embeddingInputs
                        )
                );
        log.info(
                "Embedding context prepared. embeddingInputCount={}, "
                        + "embeddingCount={}",
                embeddingInputs.size(),
                embeddingContext.embeddings().size()
        );
        List<SemanticMatchCandidate> semanticCandidates =
                new ArrayList<>();

        List<PreparedEvaluation> preparedEvaluations =
                new ArrayList<>();

        List<RequirementMatchResult> evaluations =
                new ArrayList<>();

        for (PreparedRequirement preparedRequirement :
                preparedRequirements) {

            RequirementExpression expression =
                    preparedRequirement.expression();

            RequirementMatchingResult matchingResult =
                    requirementMatcher.match(
                            expression,
                            requirementClaimTypes,
                            resumeClaims,
                            embeddingContext
                    );

            List<RequirementMatch> matches =
                    matchingResult.matches();

            List<SemanticMatchCandidate> requirementSemanticCandidates =
                    matchingResult.semanticCandidates();

            semanticCandidates.addAll(
                    requirementSemanticCandidates
            );

            preparedEvaluations.add(
                    new PreparedEvaluation(
                            preparedRequirement.source(),
                            expression,
                            matches,
                            requirementSemanticCandidates
                    )
            );
            log.debug(
                    "Requirement canonical pass completed. priority={}, value={}, "
                            + "canonicalMatches={}, semanticCandidates={}, "
                            + "canonicalCoverage={}/{}",
                    preparedRequirement.source().priority(),
                    preparedRequirement.source().requirement().value(),
                    matches.size(),
                    requirementSemanticCandidates.size(),
                    coverageCalculator.calculate(
                            expression,
                            matches
                    ).matchedComponents(),
                    coverageCalculator.calculate(
                            expression,
                            matches
                    ).totalComponents()
            );
        }

        Map<SemanticMatchCandidate, MatchRelationship>
                semanticClassifications =
                semanticCandidates.isEmpty()
                        ? Map.of()
                        : semanticBatchClassifier.classifyBatch(
                        semanticCandidates
                );
        log.info(
                "Requirement matching classified candidates. "
                        + "preparedEvaluations={}, semanticCandidates={}, "
                        + "classifications={}",
                preparedEvaluations.size(),
                semanticCandidates.size(),
                semanticClassifications.size()
        );

        for (SemanticMatchCandidate candidate : semanticCandidates) {
            log.info(
                    "Semantic classification result. "
                            + "requirement='{}', resumeCapability='{}', "
                            + "similarity={}, relationship={}",
                    candidate.requirementValue(),
                    candidate.resumeValue(),
                    candidate.similarity(),
                    semanticClassifications.get(candidate)
            );
        }
        for (PreparedEvaluation preparedEvaluation :
                preparedEvaluations) {

            List<RequirementMatch> matches =
                    new ArrayList<>(
                            preparedEvaluation.canonicalMatches()
                    );

            for (SemanticMatchCandidate candidate :
                    preparedEvaluation.semanticCandidates()) {

                MatchRelationship relationship =
                        semanticClassifications.get(candidate);

                if (relationship == null) {
                    throw new IllegalStateException(
                            "Missing semantic classification for candidate: "
                                    + candidate
                    );
                }

                matches.add(
                        new RequirementMatch(
                                candidate.requirementClaimId(),
                                candidate.requirementValue(),
                                candidate.resumeClaimId(),
                                relationship,
                                MatchingMethod.SEMANTIC
                        )
                );
            }

            RequirementCoverage coverage =
                    coverageCalculator.calculate(
                            preparedEvaluation.expression(),
                            matches
                    );
            log.info(
                    "Final requirement coverage. priority={}, value={}, "
                            + "matchedComponents={}, totalComponents={}, satisfied={}",
                    preparedEvaluation.source().priority(),
                    preparedEvaluation.source().requirement().value(),
                    coverage.matchedComponents(),
                    coverage.totalComponents(),
                    coverage.coverage()
            );

            RequirementEvaluation evaluation =
                    new RequirementEvaluation(
                            preparedEvaluation.expression(),
                            matches,
                            coverage
                    );

            log.debug(
                    "Requirement final evaluation. priority={}, value={}, "
                            + "canonicalMatches={}, acceptedSemanticMatches={}, "
                            + "finalCoverage={}/{}",
                    preparedEvaluation.source().priority(),
                    preparedEvaluation.source().requirement().value(),
                    preparedEvaluation.canonicalMatches().size(),
                    preparedEvaluation.semanticCandidates().size(),
                    coverage.matchedComponents(),
                    coverage.totalComponents()
            );

            RequirementToEvaluate source =
                    preparedEvaluation.source();

            evaluations.add(
                    new RequirementMatchResult(
                            source.requirement(),
                            source.priority(),
                            evaluation
                    )
            );
        }

        log.info(
                "Requirement matching finished. evaluationCount={}",
                evaluations.size()
        );
        return List.copyOf(evaluations);
    }

    private record PreparedRequirement(
            RequirementToEvaluate source,
            RequirementExpression expression
    ) {
    }

    private record PreparedEvaluation(
            RequirementToEvaluate source,
            RequirementExpression expression,
            List<RequirementMatch> canonicalMatches,
            List<SemanticMatchCandidate> semanticCandidates
    ) {
        private PreparedEvaluation {
            Objects.requireNonNull(
                    source,
                    "source must not be null"
            );

            Objects.requireNonNull(
                    expression,
                    "expression must not be null"
            );

            Objects.requireNonNull(
                    canonicalMatches,
                    "canonicalMatches must not be null"
            );

            Objects.requireNonNull(
                    semanticCandidates,
                    "semanticCandidates must not be null"
            );

            canonicalMatches =
                    List.copyOf(canonicalMatches);

            semanticCandidates =
                    List.copyOf(semanticCandidates);
        }
    }
}
