package com.airesumeanalyzer.backend.rie.matcher;


import org.springframework.stereotype.Component;

@Component
public final class SemanticMatchPolicy {

    private static final double DEFAULT_THRESHOLD = 0.80;

    private final double threshold;

    public SemanticMatchPolicy() {
        this(DEFAULT_THRESHOLD);
    }

    public SemanticMatchPolicy(double threshold) {
        if (threshold < -1.0 || threshold > 1.0) {
            throw new IllegalArgumentException(
                    "threshold must be between -1.0 and 1.0"
            );
        }

        this.threshold = threshold;
    }

    public boolean isCandidate(double similarity) {
        if (Double.isNaN(similarity)) {
            return false;
        }

        return similarity >= threshold;
    }

    public double threshold() {
        return threshold;
    }
}


