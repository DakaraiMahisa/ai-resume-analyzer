package com.airesumeanalyzer.backend.processing.handler;

import com.airesumeanalyzer.backend.common.enums.DocumentProcessingStatus;
import com.airesumeanalyzer.backend.processing.enums.DocumentType;

import java.util.UUID;

public interface DocumentProcessingHandler {
    boolean supports(DocumentType documentType);

    void updateStatus(
            UUID documentId,
            DocumentProcessingStatus status
    );
}
