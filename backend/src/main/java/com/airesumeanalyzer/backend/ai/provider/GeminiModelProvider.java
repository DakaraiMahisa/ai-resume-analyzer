package com.airesumeanalyzer.backend.ai.provider;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GeminiModelProvider implements AiModelProvider {

    private final ChatClient chatClient;

    @Override
    public <T> T generate(
            String prompt,
            Class<T> responseType
    ) {
        return chatClient
                .prompt()
                .user(prompt)
                .call()
                .entity(responseType);
    }
}