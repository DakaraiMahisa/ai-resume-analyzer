package com.airesumeanalyzer.backend.ai.parser;

import com.airesumeanalyzer.backend.ai.model.claims.ProposedClaim;
import com.airesumeanalyzer.backend.ai.model.understanding.StructuredJobDescription;
import com.airesumeanalyzer.backend.ai.model.understanding.StructuredResume;

import java.util.List;
import java.util.UUID;

public interface AiResponseParser {

    List<ProposedClaim> parseResume(
            StructuredResume structuredResume,
            UUID sourceDocumentId
    );

    List<ProposedClaim> parseJobDescription(
            StructuredJobDescription structuredJobDescription,
            UUID sourceDocumentId
    );
}