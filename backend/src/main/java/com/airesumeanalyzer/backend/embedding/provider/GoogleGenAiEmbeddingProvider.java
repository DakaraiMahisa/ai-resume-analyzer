package com.airesumeanalyzer.backend.embedding.provider;

import com.airesumeanalyzer.backend.rie.capability.embedding.Embedding;
import com.airesumeanalyzer.backend.rie.capability.embedding.EmbeddingProvider;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.ai.google.genai.text.GoogleGenAiTextEmbeddingModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
@ConditionalOnProperty(
        name = "app.embedding.provider",
        havingValue = "google-genai"
)
public final class GoogleGenAiEmbeddingProvider
        implements EmbeddingProvider {

    private final GoogleGenAiTextEmbeddingModel embeddingModel;
    private final String modelName;

    public GoogleGenAiEmbeddingProvider(
            GoogleGenAiTextEmbeddingModel embeddingModel,
            @Value("${spring.ai.google.genai.embedding.text.model}")
            String modelName
    ) {
        this.embeddingModel = Objects.requireNonNull(
                embeddingModel,
                "embeddingModel must not be null"
        );

        if (modelName == null || modelName.isBlank()) {
            throw new IllegalArgumentException(
                    "Google GenAI model must not be blank"
            );
        }

        this.modelName = modelName;
    }

    @Override
    public String getModelId() {
        return "google-genai:" + modelName;
    }

    @Override
    public Map<String, Embedding> embedBatch(
            Collection<String> values
    ) {
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

        EmbeddingResponse response =
                embeddingModel.embedForResponse(inputs);

        if (response == null
                || response.getResults() == null) {
            throw new IllegalStateException(
                    "Google GenAI returned an invalid embedding response"
            );
        }

        if (response.getResults().size() != inputs.size()) {
            throw new IllegalStateException(
                    "Google GenAI returned "
                            + response.getResults().size()
                            + " embeddings for "
                            + inputs.size()
                            + " inputs"
            );
        }

        Map<String, Embedding> embeddings = new LinkedHashMap<>();

        for (int i = 0; i < inputs.size(); i++) {

            org.springframework.ai.embedding.Embedding result =
                    response.getResults().get(i);

            if (result == null || result.getOutput() == null) {
                throw new IllegalStateException(
                        "Google GenAI returned an empty embedding for input: "
                                + inputs.get(i)
                );
            }

            float[] vector = result.getOutput();

            if (vector.length == 0) {
                throw new IllegalStateException(
                        "Google GenAI returned an empty embedding vector"
                );
            }

            for (float value : vector) {
                if (!Float.isFinite(value)) {
                    throw new IllegalStateException(
                            "Google GenAI returned an invalid embedding value"
                    );
                }
            }

            embeddings.put(
                    inputs.get(i),
                    new Embedding(vector)
            );
        }

        return Map.copyOf(embeddings);
    }
}