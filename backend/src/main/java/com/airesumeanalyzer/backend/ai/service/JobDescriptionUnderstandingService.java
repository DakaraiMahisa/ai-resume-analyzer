package com.airesumeanalyzer.backend.ai.service;

import com.airesumeanalyzer.backend.ai.model.understanding.StructuredJobDescription;

public interface JobDescriptionUnderstandingService {
    StructuredJobDescription understand(String rawText);
}
