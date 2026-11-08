package com.airesumeanalyzer.backend.processing.service;

import com.airesumeanalyzer.backend.common.exception.base.ConflictException;
import com.airesumeanalyzer.backend.common.exception.base.ResourceNotFoundException;
import com.airesumeanalyzer.backend.processing.entity.ProcessingJob;
import com.airesumeanalyzer.backend.processing.enums.DocumentType;
import com.airesumeanalyzer.backend.processing.enums.ProcessingJobStatus;
import com.airesumeanalyzer.backend.processing.exception.ProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DocumentProcessingServiceImplTest {

    @Mock
    private ProcessingJobService processingJobService;

    @Mock
    private ResumeProcessingService resumeProcessingService;

    @Mock
    private JobDescriptionProcessingService jobDescriptionProcessingService;

    private DocumentProcessingServiceImpl documentProcessingService;

    @BeforeEach
    void setUp() {
        documentProcessingService = new DocumentProcessingServiceImpl(
                processingJobService,
                resumeProcessingService,
                jobDescriptionProcessingService
        );
    }

    @Test
    @DisplayName("Should process a resume and complete the job")
    void shouldProcessResumeAndCompleteJob() {
        UUID jobId = UUID.randomUUID();
        UUID resumeId = UUID.randomUUID();

        ProcessingJob job = ProcessingJob.builder()
                .documentType(DocumentType.RESUME)
                .documentId(resumeId)
                .status(ProcessingJobStatus.PENDING)
                .build();

        when(processingJobService.getJob(jobId))
                .thenReturn(job);

        documentProcessingService.process(jobId);

        InOrder inOrder = inOrder(
                processingJobService,
                resumeProcessingService
        );

        inOrder.verify(processingJobService).getJob(jobId);
        inOrder.verify(processingJobService).startJob(jobId);
        inOrder.verify(resumeProcessingService).processResume(resumeId);
        inOrder.verify(processingJobService).completeJob(jobId);

        verifyNoInteractions(jobDescriptionProcessingService);
    }

    @Test
    @DisplayName("Should process a job description and complete the job")
    void shouldProcessJobDescriptionAndCompleteJob() {
        UUID jobId = UUID.randomUUID();
        UUID jobDescriptionId = UUID.randomUUID();

        ProcessingJob job = ProcessingJob.builder()
                .documentType(DocumentType.JOB_DESCRIPTION)
                .documentId(jobDescriptionId)
                .status(ProcessingJobStatus.PENDING)
                .build();

        when(processingJobService.getJob(jobId))
                .thenReturn(job);

        documentProcessingService.process(jobId);

        InOrder inOrder = inOrder(
                processingJobService,
                jobDescriptionProcessingService
        );

        inOrder.verify(processingJobService).getJob(jobId);
        inOrder.verify(processingJobService).startJob(jobId);
        inOrder.verify(jobDescriptionProcessingService)
                .processJobDescription(jobDescriptionId);
        inOrder.verify(processingJobService).completeJob(jobId);

        verifyNoInteractions(resumeProcessingService);
    }

    @Test
    @DisplayName("Should mark the job as failed and rethrow the processing exception")
    void shouldFailJobAndRethrowExceptionWhenResumeProcessingFails() {
        UUID jobId = UUID.randomUUID();
        UUID resumeId = UUID.randomUUID();

        ProcessingJob job = ProcessingJob.builder()
                .documentType(DocumentType.RESUME)
                .documentId(resumeId)
                .status(ProcessingJobStatus.PENDING)
                .build();

        ProcessingException processingException =
                new ProcessingException("Resume text extraction failed.");

        when(processingJobService.getJob(jobId))
                .thenReturn(job);

        doThrow(processingException)
                .when(resumeProcessingService)
                .processResume(resumeId);

        ProcessingException thrownException = assertThrows(
                ProcessingException.class,
                () -> documentProcessingService.process(jobId)
        );

        assertEquals(processingException, thrownException);

        verify(processingJobService).startJob(jobId);
        verify(processingJobService).failJob(
                jobId,
                "Resume text extraction failed."
        );

        verify(processingJobService, never()).completeJob(jobId);
        verifyNoInteractions(jobDescriptionProcessingService);
    }

    @Test
    @DisplayName("Should use fallback error message when exception message is null")
    void shouldUseFallbackMessageWhenExceptionMessageIsNull() {
        UUID jobId = UUID.randomUUID();
        UUID resumeId = UUID.randomUUID();

        ProcessingJob job = ProcessingJob.builder()
                .documentType(DocumentType.RESUME)
                .documentId(resumeId)
                .status(ProcessingJobStatus.PENDING)
                .build();

        RuntimeException exception = new RuntimeException((String) null);

        when(processingJobService.getJob(jobId))
                .thenReturn(job);

        doThrow(exception)
                .when(resumeProcessingService)
                .processResume(resumeId);

        RuntimeException thrownException = assertThrows(
                RuntimeException.class,
                () -> documentProcessingService.process(jobId)
        );

        assertEquals(exception, thrownException);

        verify(processingJobService).failJob(
                jobId,
                "Document processing failed."
        );

        verify(processingJobService, never()).completeJob(jobId);
    }

    @Test
    @DisplayName("Should use fallback error message when exception message is blank")
    void shouldUseFallbackMessageWhenExceptionMessageIsBlank() {
        UUID jobId = UUID.randomUUID();
        UUID resumeId = UUID.randomUUID();

        ProcessingJob job = ProcessingJob.builder()
                .documentType(DocumentType.RESUME)
                .documentId(resumeId)
                .status(ProcessingJobStatus.PENDING)
                .build();

        RuntimeException exception = new RuntimeException("   ");

        when(processingJobService.getJob(jobId))
                .thenReturn(job);

        doThrow(exception)
                .when(resumeProcessingService)
                .processResume(resumeId);

        RuntimeException thrownException = assertThrows(
                RuntimeException.class,
                () -> documentProcessingService.process(jobId)
        );

        assertEquals(exception, thrownException);

        verify(processingJobService).failJob(
                jobId,
                "Document processing failed."
        );

        verify(processingJobService, never()).completeJob(jobId);
    }

    @Test
    @DisplayName("Should propagate exception when job retrieval fails")
    void shouldPropagateExceptionWhenJobRetrievalFails() {
        UUID jobId = UUID.randomUUID();

        ResourceNotFoundException exception =
                new ResourceNotFoundException("Processing job not found.");

        when(processingJobService.getJob(jobId))
                .thenThrow(exception);

        ResourceNotFoundException thrownException = assertThrows(
                ResourceNotFoundException.class,
                () -> documentProcessingService.process(jobId)
        );

        assertEquals(exception, thrownException);

        verify(processingJobService).getJob(jobId);
        verify(processingJobService, never()).startJob(any());
        verify(processingJobService, never()).failJob(any(), any());
        verifyNoInteractions(
                resumeProcessingService,
                jobDescriptionProcessingService
        );
    }

    @Test
    @DisplayName("Should propagate exception when job startup fails")
    void shouldPropagateExceptionWhenJobStartupFails() {
        UUID jobId = UUID.randomUUID();
        UUID resumeId = UUID.randomUUID();

        ProcessingJob job = ProcessingJob.builder()
                .documentType(DocumentType.RESUME)
                .documentId(resumeId)
                .status(ProcessingJobStatus.PENDING)
                .build();

        ConflictException exception =
                new ConflictException("Processing job cannot be started.");

        when(processingJobService.getJob(jobId))
                .thenReturn(job);

        doThrow(exception)
                .when(processingJobService)
                .startJob(jobId);

        ConflictException thrownException = assertThrows(
                ConflictException.class,
                () -> documentProcessingService.process(jobId)
        );

        assertEquals(exception, thrownException);

        verify(processingJobService).getJob(jobId);
        verify(processingJobService).startJob(jobId);
        verify(processingJobService, never()).failJob(any(), any());
        verify(processingJobService, never()).completeJob(any());

        verifyNoInteractions(
                resumeProcessingService,
                jobDescriptionProcessingService
        );
    }

    @Test
    @DisplayName("Should propagate completion exception without marking job as failed")
    void shouldPropagateExceptionWhenJobCompletionFails() {
        UUID jobId = UUID.randomUUID();
        UUID resumeId = UUID.randomUUID();

        ProcessingJob job = ProcessingJob.builder()
                .documentType(DocumentType.RESUME)
                .documentId(resumeId)
                .status(ProcessingJobStatus.PENDING)
                .build();

        RuntimeException exception =
                new RuntimeException("Failed to update job completion.");

        when(processingJobService.getJob(jobId))
                .thenReturn(job);

        doThrow(exception)
                .when(processingJobService)
                .completeJob(jobId);

        RuntimeException thrownException = assertThrows(
                RuntimeException.class,
                () -> documentProcessingService.process(jobId)
        );

        assertEquals(exception, thrownException);

        verify(processingJobService).startJob(jobId);
        verify(resumeProcessingService).processResume(resumeId);
        verify(processingJobService).completeJob(jobId);

        verify(processingJobService, never()).failJob(any(), any());

        verifyNoInteractions(jobDescriptionProcessingService);
    }
}
