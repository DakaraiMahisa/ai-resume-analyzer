package com.airesumeanalyzer.backend.processing.handler;

import com.airesumeanalyzer.backend.common.enums.DocumentProcessingStatus;
import com.airesumeanalyzer.backend.common.exception.base.ResourceNotFoundException;
import com.airesumeanalyzer.backend.processing.enums.DocumentType;
import com.airesumeanalyzer.backend.resume.entity.Resume;
import com.airesumeanalyzer.backend.resume.repository.ResumeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ResumeProcessingHandler
        implements DocumentProcessingHandler {

    private final ResumeRepository resumeRepository;

    @Override
    public boolean supports(DocumentType documentType) {
        return documentType == DocumentType.RESUME;
    }

    @Override
    @Transactional
    public void updateStatus(
            UUID documentId,
            DocumentProcessingStatus status
    ) {
        Resume resume = resumeRepository.findById(documentId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Resume not found."
                        )
                );

        resume.setProcessingStatus(status);
    }
}
