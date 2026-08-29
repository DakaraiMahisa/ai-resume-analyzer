package com.airesumeanalyzer.backend.ai.evidence;


import com.airesumeanalyzer.backend.ai.model.claims.Evidence;

import java.util.List;

public interface EvidenceLocator {

    List<Evidence> locate(
            String rawText,
            String extractedValue
    );
}