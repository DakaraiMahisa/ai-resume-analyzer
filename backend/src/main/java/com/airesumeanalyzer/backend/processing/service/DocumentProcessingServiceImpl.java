package com.airesumeanalyzer.backend.processing.service;

import com.airesumeanalyzer.backend.processing.entity.ProcessingJob;
import com.airesumeanalyzer.backend.processing.exception.ProcessingException;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DocumentProcessingServiceImpl
        implements DocumentProcessingService {

    private final ProcessingJobService processingJobService;
    private final ResumeProcessingService resumeProcessingService;
    private final JobDescriptionProcessingService jobDescriptionProcessingService;

    @Override
    public void process(UUID jobId) {

        ProcessingJob job =
                processingJobService.getJob(jobId);

        processingJobService.startJob(jobId);

        try {

            switch (job.getDocumentType()) {

                case RESUME -> resumeProcessingService.processResume(
                        job.getDocumentId()
                );

                case JOB_DESCRIPTION ->
                        jobDescriptionProcessingService.processJobDescription(
                        job.getDocumentId()
                );

                default -> throw new ProcessingException(
                        "Unsupported document type: "
                                + job.getDocumentType()
                );
            }
            processingJobService.completeJob(jobId);

        } catch (Exception exception) {

            String errorMessage = exception.getMessage();

            if (errorMessage == null || errorMessage.isBlank()) {
                errorMessage = "Document processing failed.";
            }

            processingJobService.failJob(
                    jobId,
                    errorMessage
            );

            throw exception;
        }
    }
}
