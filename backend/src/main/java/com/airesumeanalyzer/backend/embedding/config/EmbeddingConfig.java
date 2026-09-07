package com.airesumeanalyzer.backend.embedding.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class EmbeddingConfig {

    @Bean
    public RestClient.Builder embeddingRestClientBuilder() {
        return RestClient.builder();
    }
}