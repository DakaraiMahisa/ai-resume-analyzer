package com.airesumeanalyzer.backend.embedding.provider;


import com.airesumeanalyzer.backend.embedding.config.OllamaEmbeddingProperties;
import com.airesumeanalyzer.backend.rie.capability.embedding.Embedding;
import com.airesumeanalyzer.backend.rie.capability.embedding.EmbeddingProvider;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.*;

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
    public Map<String, Embedding> embedBatch(Collection<String> values) {
        Objects.requireNonNull(values, "values must not be null");

        if (values.isEmpty()) {
            return Map.of();
        }

        List<String> inputs = values.stream()
                .map(value -> {
                    if (value == null || value.isBlank()) {
                        throw new IllegalArgumentException(
                                "embedding input must not be null or blank"
                        );
                    }
                    return value.trim();
                })
                .distinct()
                .toList();

        OllamaEmbeddingRequest request =
                new OllamaEmbeddingRequest(
                        properties.model(),
                        inputs
                );

        OllamaEmbeddingResponse response =
                restClient.post()
                        .uri("/api/embed")
                        .body(request)
                        .retrieve()
                        .body(OllamaEmbeddingResponse.class);

        if (response == null
                || response.embeddings() == null) {
            throw new IllegalStateException(
                    "Ollama returned an invalid embedding response"
            );
        }

        if (response.embeddings().size() != inputs.size()) {
            throw new IllegalStateException(
                    "Ollama returned "
                            + response.embeddings().size()
                            + " embeddings for "
                            + inputs.size()
                            + " inputs"
            );
        }

        Map<String, Embedding> embeddings = new LinkedHashMap<>();

        for (int i = 0; i < inputs.size(); i++) {
            List<Float> vector = response.embeddings().get(i);

            if (vector == null || vector.isEmpty()) {
                throw new IllegalStateException(
                        "Ollama returned an empty embedding for input: "
                                + inputs.get(i)
                );
            }

            float[] valuesArray = new float[vector.size()];

            for (int j = 0; j < vector.size(); j++) {
                Float value = vector.get(j);

                if (value == null || !Float.isFinite(value)) {
                    throw new IllegalStateException(
                            "Ollama returned an invalid embedding value"
                    );
                }

                valuesArray[j] = value;
            }

            embeddings.put(
                    inputs.get(i),
                    new Embedding(valuesArray)
            );
        }

        return Map.copyOf(embeddings);
    }

    private record OllamaEmbeddingRequest(
            String model,
            List<String> input
    ) {
    }

    private record OllamaEmbeddingResponse(
            List<List<Float>> embeddings
    ) {
    }
}

