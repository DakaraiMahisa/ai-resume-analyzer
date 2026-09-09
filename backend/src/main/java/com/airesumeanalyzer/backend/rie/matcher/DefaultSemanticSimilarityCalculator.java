package com.airesumeanalyzer.backend.rie.matcher;

import com.airesumeanalyzer.backend.rie.capability.embedding.Embedding;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public final class DefaultSemanticSimilarityCalculator
        implements SemanticSimilarityCalculator {

    @Override
    public double calculate(
            Embedding left,
            Embedding right
    ) {
        Objects.requireNonNull(
                left,
                "left embedding must not be null"
        );

        Objects.requireNonNull(
                right,
                "right embedding must not be null"
        );

        float[] leftVector = left.vector();
        float[] rightVector = right.vector();

        if (leftVector.length != rightVector.length) {
            throw new IllegalArgumentException(
                    "Embedding vectors must have the same dimension"
            );
        }

        double dotProduct = 0.0;
        double leftMagnitudeSquared = 0.0;
        double rightMagnitudeSquared = 0.0;

        for (int i = 0; i < leftVector.length; i++) {
            double leftValue = leftVector[i];
            double rightValue = rightVector[i];

            dotProduct += leftValue * rightValue;
            leftMagnitudeSquared += leftValue * leftValue;
            rightMagnitudeSquared += rightValue * rightValue;
        }

        if (leftMagnitudeSquared == 0.0
                || rightMagnitudeSquared == 0.0) {
            throw new IllegalArgumentException(
                    "Embedding vector must not be a zero vector"
            );
        }

        return dotProduct
                / (Math.sqrt(leftMagnitudeSquared)
                * Math.sqrt(rightMagnitudeSquared));
    }
}

