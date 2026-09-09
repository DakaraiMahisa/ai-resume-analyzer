package com.airesumeanalyzer.backend.processing.handler;

import com.airesumeanalyzer.backend.common.enums.DocumentProcessingStatus;
import com.airesumeanalyzer.backend.common.exception.base.ResourceNotFoundException;
import com.airesumeanalyzer.backend.jobdescription.entity.JobDescription;
import com.airesumeanalyzer.backend.jobdescription.repository.JobDescriptionRepository;
import com.airesumeanalyzer.backend.processing.enums.DocumentType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JobDescriptionProcessingHandler
        implements DocumentProcessingHandler {

    private final JobDescriptionRepository jobDescriptionRepository;

    @Override
    public boolean supports(DocumentType documentType) {
        return documentType == DocumentType.JOB_DESCRIPTION;
    }

    @Override
    @Transactional
    public void updateStatus(
            UUID documentId,
            DocumentProcessingStatus status
    ) {
        JobDescription jobDescription =
                jobDescriptionRepository.findById(documentId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Job description not found."
                                )
                        );

        jobDescription.setProcessingStatus(status);
    }
}
