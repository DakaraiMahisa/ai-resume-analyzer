package com.airesumeanalyzer.backend.rie.capability;

import com.airesumeanalyzer.backend.ai.domain.entity.Claim;
import com.airesumeanalyzer.backend.rie.capability.embedding.Embedding;
import com.airesumeanalyzer.backend.rie.domain.RequirementComponent;
import com.airesumeanalyzer.backend.rie.matcher.EmbeddingContext;
import com.airesumeanalyzer.backend.rie.matcher.SemanticMatchPolicy;
import com.airesumeanalyzer.backend.rie.matcher.SemanticSimilarityCalculator;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

@Component
public final class SemanticCapabilityMatcher {

    private final SemanticSimilarityCalculator similarityCalculator;
    private final SemanticMatchPolicy matchPolicy;

    public SemanticCapabilityMatcher(
            SemanticSimilarityCalculator similarityCalculator,
            SemanticMatchPolicy matchPolicy
    ) {


        this.similarityCalculator =
                Objects.requireNonNull(
                        similarityCalculator,
                        "similarityCalculator must not be null"
                );

        this.matchPolicy =
                Objects.requireNonNull(
                        matchPolicy,
                        "matchPolicy must not be null"
                );

    }

    public Optional<SemanticMatchCandidate> match(
            RequirementComponent requirement,
            Claim resumeClaim,
            EmbeddingContext embeddingContext
    ) {
        Objects.requireNonNull(
                requirement,
                "requirement must not be null"
        );

        Objects.requireNonNull(
                resumeClaim,
                "resumeClaim must not be null"
        );

        Objects.requireNonNull(
                embeddingContext,
                "embeddingContext must not be null"
        );

        String requirementValue =
                requirement.value();

        String resumeValue =
                resumeClaim.getCanonicalName();

        if (requirementValue == null
                || requirementValue.isBlank()
                || resumeValue == null
                || resumeValue.isBlank()) {
            return Optional.empty();
        }

        Embedding requirementEmbedding =
                embeddingContext.get(requirementValue);

        Embedding resumeEmbedding =
                embeddingContext.get(resumeValue);

        double similarity =
                similarityCalculator.calculate(
                        requirementEmbedding,
                        resumeEmbedding
                );

        if (!matchPolicy.isCandidate(similarity)) {
            return Optional.empty();
        }

        return Optional.of(
                new SemanticMatchCandidate(
                        requirement.requirementClaimId(),
                        requirementValue,
                        resumeClaim.getId(),
                        resumeValue,
                        similarity
                )
        );
    }
}