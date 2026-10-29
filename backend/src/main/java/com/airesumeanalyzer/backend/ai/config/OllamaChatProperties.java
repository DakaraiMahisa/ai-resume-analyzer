package com.airesumeanalyzer.backend.ai.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "ollama.chat")
public record OllamaChatProperties(
        String baseUrl,
        String model,
        Duration readTimeout
) {
}