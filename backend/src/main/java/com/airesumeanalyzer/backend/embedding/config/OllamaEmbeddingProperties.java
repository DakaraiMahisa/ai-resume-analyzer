package com.airesumeanalyzer.backend.embedding.config;


import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "ollama.embedding")
public record OllamaEmbeddingProperties(
        String baseUrl,
        String model
) {
}

