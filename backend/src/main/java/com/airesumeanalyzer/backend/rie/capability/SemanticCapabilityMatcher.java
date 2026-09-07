package com.airesumeanalyzer.backend.rie.capability;

import com.airesumeanalyzer.backend.ai.domain.entity.Claim;
import com.airesumeanalyzer.backend.rie.capability.embedding.Embedding;
import com.airesumeanalyzer.backend.rie.capability.embedding.EmbeddingProvider;

import com.airesumeanalyzer.backend.rie.domain.MatchRelationship;
import com.airesumeanalyzer.backend.rie.domain.MatchingMethod;
import com.airesumeanalyzer.backend.rie.domain.RequirementComponent;
import com.airesumeanalyzer.backend.rie.domain.RequirementMatch;
import com.airesumeanalyzer.backend.rie.matcher.SemanticClassifier;
import com.airesumeanalyzer.backend.rie.matcher.SemanticMatchPolicy;
import com.airesumeanalyzer.backend.rie.matcher.SemanticSimilarityCalculator;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

@Component
public final class SemanticCapabilityMatcher
        implements CapabilityMatcher {

    private final EmbeddingProvider embeddingProvider;
    private final SemanticSimilarityCalculator similarityCalculator;
    private final SemanticMatchPolicy matchPolicy;
    private final SemanticClassifier semanticClassifier;

    public SemanticCapabilityMatcher(
            EmbeddingProvider embeddingProvider,
            SemanticSimilarityCalculator similarityCalculator,
            SemanticMatchPolicy matchPolicy,
            SemanticClassifier semanticClassifier
    ) {
        this.embeddingProvider =
                Objects.requireNonNull(
                        embeddingProvider,
                        "embeddingProvider must not be null"
                );

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

        this.semanticClassifier =
                Objects.requireNonNull(
                        semanticClassifier,
                        "semanticClassifier must not be null"
                );
    }

    @Override
    public Optional<RequirementMatch> match(
            RequirementComponent requirement,
            Claim resumeClaim
    ) {
        Objects.requireNonNull(
                requirement,
                "requirement must not be null"
        );

        Objects.requireNonNull(
                resumeClaim,
                "resumeClaim must not be null"
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
                embeddingProvider.embed(requirementValue);

        Embedding resumeEmbedding =
                embeddingProvider.embed(resumeValue);

        double similarity =
                similarityCalculator.calculate(
                        requirementEmbedding,
                        resumeEmbedding
                );

        if (!matchPolicy.isCandidate(similarity)) {
            return Optional.empty();
        }

        MatchRelationship relationship =
                semanticClassifier.classify(
                        requirementValue,
                        resumeValue,
                        similarity
                );

        return Optional.of(
                new RequirementMatch(
                        requirement.requirementClaimId(),
                        requirementValue,
                        resumeClaim.getId(),
                        relationship,
                        MatchingMethod.SEMANTIC
                )
        );
    }
}