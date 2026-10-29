package com.airesumeanalyzer.backend.ai.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaChatOptions;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class AiConfig {

    @Bean("geminiChatClient")
    public ChatClient geminiChatClient(
            @Qualifier("googleGenAiChatModel")
            ChatModel chatModel
    ) {
        return ChatClient.builder(chatModel)
                .build();
    }

    @Bean
    public OllamaChatModel ollamaChatModel(
            RestClient.Builder restClientBuilder,
            OllamaChatProperties properties
    ) {
        SimpleClientHttpRequestFactory requestFactory =
                new SimpleClientHttpRequestFactory();

        requestFactory.setReadTimeout(properties.readTimeout());

        RestClient.Builder ollamaRestClientBuilder =
                restClientBuilder
                        .clone()
                        .requestFactory(requestFactory);

        OllamaApi ollamaApi =
                OllamaApi.builder()
                        .baseUrl(properties.baseUrl())
                        .restClientBuilder(ollamaRestClientBuilder)
                        .build();

        OllamaChatOptions options =
                OllamaChatOptions.builder()
                        .model(properties.model())
                        .build();

        return OllamaChatModel.builder()
                .ollamaApi(ollamaApi)
                .options(options)
                .build();
    }

    @Bean("ollamaChatClient")
    public ChatClient ollamaChatClient(
            OllamaChatModel ollamaChatModel
    ) {
        return ChatClient.builder(ollamaChatModel)
                .build();
    }
}
