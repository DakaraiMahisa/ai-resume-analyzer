package com.airesumeanalyzer.backend.processing.service;

import com.airesumeanalyzer.backend.common.enums.DocumentProcessingStatus;
import com.airesumeanalyzer.backend.common.exception.base.BadRequestException;
import com.airesumeanalyzer.backend.common.exception.base.ConflictException;
import com.airesumeanalyzer.backend.common.exception.base.ResourceNotFoundException;
import com.airesumeanalyzer.backend.processing.entity.ProcessingJob;
import com.airesumeanalyzer.backend.processing.enums.DocumentType;
import com.airesumeanalyzer.backend.processing.enums.ProcessingJobStatus;
import com.airesumeanalyzer.backend.processing.enums.ProcessingStage;
import com.airesumeanalyzer.backend.processing.handler.DocumentProcessingHandler;
import com.airesumeanalyzer.backend.processing.repository.ProcessingJobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProcessingJobService {

    private final ProcessingJobRepository processingJobRepository;
    private final List<DocumentProcessingHandler> documentProcessingHandlers;

    public ProcessingJob createJob(
            DocumentType documentType,
            UUID documentId
    ) {

        if (documentType == null) {
            throw new BadRequestException(
                    "Document type must not be null."
            );
        }

        if (documentId == null) {
            throw new BadRequestException(
                    "Document ID must not be null."
            );
        }

        ProcessingJob job = ProcessingJob.builder()
                .documentType(documentType)
                .documentId(documentId)
                .status(ProcessingJobStatus.PENDING)
                .currentStage(ProcessingStage.INITIALIZATION)
                .progress(0)
                .retryCount(0)
                .build();

        return processingJobRepository.save(job);
    }

    @Transactional
    public ProcessingJob startJob(UUID jobId) {

        ProcessingJob job = processingJobRepository.findById(jobId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Processing job not found.")
                );

        if (job.getStatus() != ProcessingJobStatus.PENDING) {
            throw new ConflictException(
                    "Processing job cannot be started because it is not pending."
            );
        }
        DocumentProcessingHandler handler =
                getHandler(job.getDocumentType());

        job.setStatus(ProcessingJobStatus.RUNNING);
        job.setStartedAt(Instant.now());

        handler.updateStatus(
                job.getDocumentId(),
                DocumentProcessingStatus.PROCESSING
        );
        return processingJobRepository.save(job);
    }


    @Transactional
    public ProcessingJob updateProgress(
            UUID jobId,
            ProcessingStage stage,
            int progress
    ) {
        ProcessingJob job = processingJobRepository.findById(jobId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Processing job not found."
                        )
                );

        if (job.getStatus() != ProcessingJobStatus.RUNNING) {
            throw new ConflictException(
                    "Processing job must be running to update progress."
            );
        }

        if (stage == null) {
            throw new BadRequestException(
                    "Processing stage must not be null."
            );
        }

        if (progress < 0 || progress > 100) {
            throw new BadRequestException(
                    "Processing progress must be between 0 and 100."
            );
        }

        job.setCurrentStage(stage);
        job.setProgress(progress);

        return processingJobRepository.save(job);
    }

    @Transactional
    public ProcessingJob completeJob(UUID jobId) {

        ProcessingJob job = processingJobRepository.findById(jobId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Processing job not found."
                        )
                );

        if (job.getStatus() != ProcessingJobStatus.RUNNING) {
            throw new ConflictException(
                    "Processing job must be running to be completed."
            );
        }
        DocumentProcessingHandler handler =
                getHandler(job.getDocumentType());

        job.setStatus(ProcessingJobStatus.COMPLETED);
        job.setCurrentStage(ProcessingStage.COMPLETED);
        job.setProgress(100);
        job.setFinishedAt(Instant.now());
        job.setErrorMessage(null);

        handler.updateStatus(
                job.getDocumentId(),
                DocumentProcessingStatus.COMPLETED
        );
        return processingJobRepository.save(job);
    }

    @Transactional
    public ProcessingJob failJob(
            UUID jobId,
            String errorMessage
    ) {
        ProcessingJob job = processingJobRepository.findById(jobId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Processing job not found."
                        )
                );

        if (job.getStatus() != ProcessingJobStatus.RUNNING) {
            throw new ConflictException(
                    "Processing job must be running to be marked as failed."
            );
        }

        if (errorMessage == null || errorMessage.isBlank()) {
            throw new BadRequestException(
                    "Error message must not be empty."
            );
        }
        DocumentProcessingHandler handler =
                getHandler(job.getDocumentType());

        job.setStatus(ProcessingJobStatus.FAILED);
        job.setFinishedAt(Instant.now());
        job.setErrorMessage(errorMessage);

        handler.updateStatus(
                job.getDocumentId(),
                DocumentProcessingStatus.FAILED
        );
        return processingJobRepository.save(job);
    }

    @Transactional(readOnly = true)
    public ProcessingJob getLatestJob(
            DocumentType documentType,
            UUID documentId
    ) {
        return processingJobRepository
                .findFirstByDocumentTypeAndDocumentIdOrderByCreatedAtDesc(
                        documentType,
                        documentId
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Processing job not found."
                        )
                );
    }

    public ProcessingJob getJob(UUID jobId) {

        return processingJobRepository.findById(jobId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Processing job not found."
                        )
                );
    }

    private DocumentProcessingHandler getHandler(
            DocumentType documentType
    ) {
        return documentProcessingHandlers.stream()
                .filter(handler -> handler.supports(documentType))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalStateException(
                                "No processing handler found for document type: "
                                        + documentType
                        )
                );
    }
}
