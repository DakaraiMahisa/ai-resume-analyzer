package com.airesumeanalyzer.backend.embedding.service;

import com.airesumeanalyzer.backend.rie.capability.embedding.Embedding;

import java.util.Collection;
import java.util.Map;

public interface EmbeddingService {
    Map<String, Embedding> embedBatch(Collection<String> values);
}
