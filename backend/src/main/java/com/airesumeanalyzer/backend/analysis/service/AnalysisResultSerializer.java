package com.airesumeanalyzer.backend.analysis.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
public class AnalysisResultSerializer {

    private final ObjectMapper objectMapper;

    public String serialize(Object result) {
        try {
            return objectMapper.writeValueAsString(result);
        } catch (Exception exception) {
            throw new IllegalStateException(
                    "Failed to serialize analysis result",
                    exception
            );
        }
    }

    public <T> T deserialize(
            String json,
            Class<T> targetType
    ) {
        try {
            return objectMapper.readValue(
                    json,
                    targetType
            );
        } catch (Exception exception) {
            throw new IllegalStateException(
                    "Failed to deserialize analysis result",
                    exception
            );
        }
    }
}
