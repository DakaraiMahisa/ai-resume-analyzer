package com.airesumeanalyzer.backend.ai.provider;


public interface AiModelProvider {

    <T> T generate(
            String prompt,
            Class<T> responseType
    );
}