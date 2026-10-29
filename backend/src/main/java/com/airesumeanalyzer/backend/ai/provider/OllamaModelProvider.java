package com.airesumeanalyzer.backend.ai.provider;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component("ollamaModelProvider")
public class OllamaModelProvider
        implements AiModelProvider {

    private final ChatClient chatClient;

    public OllamaModelProvider(
            @Qualifier("ollamaChatClient")
            ChatClient chatClient
    ) {
        this.chatClient =
                Objects.requireNonNull(
                        chatClient,
                        "chatClient must not be null"
                );
    }
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