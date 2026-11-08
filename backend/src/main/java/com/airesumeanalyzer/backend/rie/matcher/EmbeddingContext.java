package com.airesumeanalyzer.backend.rie.matcher;

import com.airesumeanalyzer.backend.rie.capability.embedding.Embedding;

import java.util.Map;
import java.util.Objects;

public record EmbeddingContext(
        Map<String, Embedding> embeddings
) {
    public EmbeddingContext {
        Objects.requireNonNull(
                embeddings,
                "embeddings must not be null"
        );

        embeddings = Map.copyOf(embeddings);
    }

    public Embedding get(String value) {
        Objects.requireNonNull(
                value,
                "value must not be null"
        );

        Embedding embedding =
                embeddings.get(value.trim());

        if (embedding == null) {
            throw new IllegalStateException(
                    "No embedding available for value: " + value
            );
        }

        return embedding;
    }
}