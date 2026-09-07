package com.airesumeanalyzer.backend.rie.capability.embedding;

import java.util.Objects;

public record Embedding(float[] vector) {

    public Embedding {
        Objects.requireNonNull(
                vector,
                "vector must not be null"
        );

        if (vector.length == 0) {
            throw new IllegalArgumentException(
                    "vector must not be empty"
            );
        }

        vector = vector.clone();
    }

    @Override
    public float[] vector() {
        return vector.clone();
    }
}
