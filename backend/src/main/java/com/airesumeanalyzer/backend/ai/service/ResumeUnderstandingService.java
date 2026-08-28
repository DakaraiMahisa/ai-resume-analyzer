package com.airesumeanalyzer.backend.ai.service;

import com.airesumeanalyzer.backend.ai.model.understanding.StructuredResume;

public interface ResumeUnderstandingService {
    StructuredResume understand(String rawText);
}
