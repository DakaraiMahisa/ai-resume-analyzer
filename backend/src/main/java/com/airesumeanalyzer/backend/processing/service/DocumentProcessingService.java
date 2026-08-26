package com.airesumeanalyzer.backend.processing.service;

import java.util.UUID;

public interface DocumentProcessingService {
    void process(UUID jobId);
}
