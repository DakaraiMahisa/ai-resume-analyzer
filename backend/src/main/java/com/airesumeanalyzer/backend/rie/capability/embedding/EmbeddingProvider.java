package com.airesumeanalyzer.backend.rie.capability.embedding;

public interface EmbeddingProvider {

    Embedding embed(String value);
}