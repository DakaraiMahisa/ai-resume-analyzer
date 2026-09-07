package com.airesumeanalyzer.backend.embedding.provider;


import com.airesumeanalyzer.backend.embedding.config.OllamaEmbeddingProperties;
import com.airesumeanalyzer.backend.rie.capability.embedding.Embedding;
import com.airesumeanalyzer.backend.rie.capability.embedding.EmbeddingProvider;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Objects;

@Component
public final class OllamaEmbeddingProvider
        implements EmbeddingProvider {

    private final RestClient restClient;
    private final OllamaEmbeddingProperties properties;

    public OllamaEmbeddingProvider(
            RestClient.Builder embeddingRestClientBuilder,
            OllamaEmbeddingProperties properties
    ) {
        this.properties =
                Objects.requireNonNull(
                        properties,
                        "properties must not be null"
                );

        if (properties.baseUrl() == null
                || properties.baseUrl().isBlank()) {
            throw new IllegalArgumentException(
                    "Ollama baseUrl must not be blank"
            );
        }

        if (properties.model() == null
                || properties.model().isBlank()) {
            throw new IllegalArgumentException(
                    "Ollama model must not be blank"
            );
        }

        this.restClient =
                embeddingRestClientBuilder
                        .baseUrl(properties.baseUrl())
                        .build();
    }

    @Override
    public Embedding embed(String value) {

        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    "value must not be blank"
            );
        }

        OllamaEmbeddingRequest request =
                new OllamaEmbeddingRequest(
                        properties.model(),
                        value.trim()
                );

        OllamaEmbeddingResponse response =
                restClient.post()
                        .uri("/api/embed")
                        .body(request)
                        .retrieve()
                        .body(OllamaEmbeddingResponse.class);

        if (response == null
                || response.embeddings() == null
                || response.embeddings().isEmpty()
                || response.embeddings().getFirst() == null
                || response.embeddings().getFirst().isEmpty()) {
            throw new IllegalStateException(
                    "Ollama returned an empty embedding"
            );
        }

        List<Float> vector =
                response.embeddings().getFirst();

        float[] values = new float[vector.size()];

        for (int i = 0; i < vector.size(); i++) {
            Float valueAtIndex = vector.get(i);

            if (valueAtIndex == null
                    || !Float.isFinite(valueAtIndex)) {
                throw new IllegalStateException(
                        "Ollama returned an invalid embedding value"
                );
            }

            values[i] = valueAtIndex;
        }

        return new Embedding(values);
    }

    private record OllamaEmbeddingRequest(
            String model,
            String input
    ) {
    }

    private record OllamaEmbeddingResponse(
            List<List<Float>> embeddings
    ) {
    }
}

