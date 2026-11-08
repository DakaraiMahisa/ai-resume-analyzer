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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProcessingJobServiceTest {

    @Mock
    private ProcessingJobRepository processingJobRepository;

    @Mock
    private DocumentProcessingHandler documentProcessingHandler;

    private ProcessingJobService processingJobService;

    @BeforeEach
    void setUp() {
        processingJobService = new ProcessingJobService(
                processingJobRepository,
                List.of(documentProcessingHandler)
        );
    }

    @Nested
    @DisplayName("createJob()")
    class CreateJobTests {

        @Test
        @DisplayName("Should create a pending processing job")
        void shouldCreatePendingJob() {

            DocumentType documentType = DocumentType.RESUME;
            UUID documentId = UUID.randomUUID();

            when(processingJobRepository.save(any(ProcessingJob.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            ProcessingJob result = processingJobService.createJob(
                    documentType,
                    documentId
            );

            assertAll(
                    () -> assertEquals(documentType, result.getDocumentType()),
                    () -> assertEquals(documentId, result.getDocumentId()),
                    () -> assertEquals(ProcessingJobStatus.PENDING, result.getStatus()),
                    () -> assertEquals(ProcessingStage.INITIALIZATION, result.getCurrentStage()),
                    () -> assertEquals(0, result.getProgress()),
                    () -> assertEquals(0, result.getRetryCount())
            );

            verify(processingJobRepository).save(any(ProcessingJob.class));
        }

        @Test
        @DisplayName("Should reject null document type")
        void shouldRejectNullDocumentType() {

            UUID documentId = UUID.randomUUID();

            assertThrows(
                    BadRequestException.class,
                    () -> processingJobService.createJob(null, documentId)
            );

            verifyNoInteractions(processingJobRepository);
        }

        @Test
        @DisplayName("Should reject null document ID")
        void shouldRejectNullDocumentId() {

            DocumentType documentType = DocumentType.RESUME;

            assertThrows(
                    BadRequestException.class,
                    () -> processingJobService.createJob(documentType, null)
            );

            verifyNoInteractions(processingJobRepository);
        }
    }


    @Nested
    @DisplayName("startJob()")
    class StartJobTests {

        @Test
        @DisplayName("Should start a pending processing job")
        void shouldStartPendingJob() {

            UUID jobId = UUID.randomUUID();
            UUID documentId = UUID.randomUUID();

            ProcessingJob job = ProcessingJob.builder()
                    .documentType(DocumentType.RESUME)
                    .documentId(documentId)
                    .status(ProcessingJobStatus.PENDING)
                    .currentStage(ProcessingStage.INITIALIZATION)
                    .progress(0)
                    .retryCount(0)
                    .build();

            when(processingJobRepository.findById(jobId))
                    .thenReturn(Optional.of(job));

            when(documentProcessingHandler.supports(DocumentType.RESUME))
                    .thenReturn(true);

            when(processingJobRepository.save(any(ProcessingJob.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            ProcessingJob result = processingJobService.startJob(jobId);

            assertAll(
                    () -> assertEquals(ProcessingJobStatus.RUNNING, result.getStatus()),
                    () -> assertNotNull(result.getStartedAt()),
                    () -> assertEquals(documentId, result.getDocumentId())
            );

            verify(documentProcessingHandler).updateStatus(
                    documentId,
                    DocumentProcessingStatus.PROCESSING
            );

            verify(processingJobRepository).save(job);
        }

        @Test
        @DisplayName("Should throw exception when processing job does not exist")
        void shouldRejectNonExistentJob() {

            UUID jobId = UUID.randomUUID();

            when(processingJobRepository.findById(jobId))
                    .thenReturn(Optional.empty());

            assertThrows(
                    ResourceNotFoundException.class,
                    () -> processingJobService.startJob(jobId)
            );

            verify(processingJobRepository, never()).save(any());
            verifyNoInteractions(documentProcessingHandler);
        }

        @Test
        @DisplayName("Should reject a job that is not pending")
        void shouldRejectNonPendingJob() {

            UUID jobId = UUID.randomUUID();

            ProcessingJob job = ProcessingJob.builder()
                    .documentType(DocumentType.RESUME)
                    .documentId(UUID.randomUUID())
                    .status(ProcessingJobStatus.RUNNING)
                    .build();

            when(processingJobRepository.findById(jobId))
                    .thenReturn(Optional.of(job));

            assertThrows(
                    ConflictException.class,
                    () -> processingJobService.startJob(jobId)
            );

            verify(processingJobRepository, never()).save(any());
            verifyNoInteractions(documentProcessingHandler);
        }

        @Test
        @DisplayName("Should reject starting a job when no handler supports its document type")
        void shouldRejectJobWhenNoHandlerExists() {

            UUID jobId = UUID.randomUUID();

            ProcessingJob job = ProcessingJob.builder()
                    .documentType(DocumentType.RESUME)
                    .documentId(UUID.randomUUID())
                    .status(ProcessingJobStatus.PENDING)
                    .build();

            when(processingJobRepository.findById(jobId))
                    .thenReturn(Optional.of(job));

            when(documentProcessingHandler.supports(DocumentType.RESUME))
                    .thenReturn(false);

            assertThrows(
                    IllegalStateException.class,
                    () -> processingJobService.startJob(jobId)
            );

            verify(processingJobRepository, never()).save(any());
            verify(documentProcessingHandler, never())
                    .updateStatus(any(), any());
        }
    }


    @Nested
    @DisplayName("updateProgress()")
    class UpdateProgressTests {

        private ProcessingJob createRunningJob() {
            return ProcessingJob.builder()
                    .documentType(DocumentType.RESUME)
                    .documentId(UUID.randomUUID())
                    .status(ProcessingJobStatus.RUNNING)
                    .currentStage(ProcessingStage.INITIALIZATION)
                    .progress(0)
                    .retryCount(0)
                    .build();
        }

        @Test
        @DisplayName("Should update processing stage and progress")
        void shouldUpdateProgress() {

            UUID jobId = UUID.randomUUID();
            ProcessingJob job = createRunningJob();

            when(processingJobRepository.findById(jobId))
                    .thenReturn(Optional.of(job));

            when(processingJobRepository.save(any(ProcessingJob.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));


            ProcessingJob result = processingJobService.updateProgress(
                    jobId,
                    ProcessingStage.TEXT_EXTRACTION,
                    50
            );

            assertAll(
                    () -> assertEquals(ProcessingStage.TEXT_EXTRACTION, result.getCurrentStage()),
                    () -> assertEquals(50, result.getProgress()),
                    () -> assertEquals(ProcessingJobStatus.RUNNING, result.getStatus())
            );

            verify(processingJobRepository).save(job);
        }

        @Test
        @DisplayName("Should allow progress at zero")
        void shouldAllowZeroProgress() {
            assertValidProgress(0);
        }

        @Test
        @DisplayName("Should allow progress at one hundred")
        void shouldAllowOneHundredProgress() {
            assertValidProgress(100);
        }

        @Test
        @DisplayName("Should reject negative progress")
        void shouldRejectNegativeProgress() {
            assertInvalidProgress(-1);
        }

        @Test
        @DisplayName("Should reject progress above one hundred")
        void shouldRejectProgressAboveOneHundred() {
            assertInvalidProgress(101);
        }

        @Test
        @DisplayName("Should reject null processing stage")
        void shouldRejectNullStage() {

            UUID jobId = UUID.randomUUID();
            ProcessingJob job = createRunningJob();

            when(processingJobRepository.findById(jobId))
                    .thenReturn(Optional.of(job));

            assertThrows(
                    BadRequestException.class,
                    () -> processingJobService.updateProgress(jobId, null, 50)
            );

            verify(processingJobRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should reject progress update when job is not running")
        void shouldRejectUpdateWhenJobIsNotRunning() {

            UUID jobId = UUID.randomUUID();

            ProcessingJob job = ProcessingJob.builder()
                    .documentType(DocumentType.RESUME)
                    .documentId(UUID.randomUUID())
                    .status(ProcessingJobStatus.PENDING)
                    .build();

            when(processingJobRepository.findById(jobId))
                    .thenReturn(Optional.of(job));

            assertThrows(
                    ConflictException.class,
                    () -> processingJobService.updateProgress(
                            jobId,
                            ProcessingStage.TEXT_EXTRACTION,
                            50
                    )
            );

            verify(processingJobRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should reject update when job does not exist")
        void shouldRejectUpdateForNonExistentJob() {

            UUID jobId = UUID.randomUUID();

            when(processingJobRepository.findById(jobId))
                    .thenReturn(Optional.empty());

            assertThrows(
                    ResourceNotFoundException.class,
                    () -> processingJobService.updateProgress(
                            jobId,
                            ProcessingStage.TEXT_EXTRACTION,
                            50
                    )
            );

            verify(processingJobRepository, never()).save(any());
        }

        private void assertValidProgress(int progress) {

            UUID jobId = UUID.randomUUID();
            ProcessingJob job = createRunningJob();

            when(processingJobRepository.findById(jobId))
                    .thenReturn(Optional.of(job));

            when(processingJobRepository.save(any(ProcessingJob.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            ProcessingJob result = processingJobService.updateProgress(
                    jobId,
                    ProcessingStage.TEXT_EXTRACTION,
                    progress
            );

            assertEquals(progress, result.getProgress());
            verify(processingJobRepository).save(job);
        }

        private void assertInvalidProgress(int progress) {

            UUID jobId = UUID.randomUUID();
            ProcessingJob job = createRunningJob();

            when(processingJobRepository.findById(jobId))
                    .thenReturn(Optional.of(job));


            assertThrows(
                    BadRequestException.class,
                    () -> processingJobService.updateProgress(
                            jobId,
                            ProcessingStage.TEXT_EXTRACTION,
                            progress
                    )
            );

            verify(processingJobRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("completeJob()")
    class CompleteJobTests {

        private ProcessingJob createRunningJob() {
            return ProcessingJob.builder()
                    .documentType(DocumentType.RESUME)
                    .documentId(UUID.randomUUID())
                    .status(ProcessingJobStatus.RUNNING)
                    .currentStage(ProcessingStage.TEXT_EXTRACTION)
                    .progress(50)
                    .retryCount(0)
                    .errorMessage("Previous error")
                    .build();
        }

        @Test
        @DisplayName("Should complete a running job")
        void shouldCompleteRunningJob() {
            ProcessingJob job = createRunningJob();

            when(processingJobRepository.findById(any()))
                    .thenReturn(Optional.of(job));
            when(documentProcessingHandler.supports(DocumentType.RESUME))
                    .thenReturn(true);
            when(processingJobRepository.save(any()))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            ProcessingJob result = processingJobService.completeJob(UUID.randomUUID());

            assertAll(
                    () -> assertEquals(ProcessingJobStatus.COMPLETED, result.getStatus()),
                    () -> assertEquals(ProcessingStage.COMPLETED, result.getCurrentStage()),
                    () -> assertEquals(100, result.getProgress()),
                    () -> assertNotNull(result.getFinishedAt()),
                    () -> assertEquals(null, result.getErrorMessage())
            );

            verify(documentProcessingHandler).updateStatus(
                    job.getDocumentId(),
                    DocumentProcessingStatus.COMPLETED
            );
            verify(processingJobRepository).save(job);
        }

        @Test
        @DisplayName("Should throw when job does not exist")
        void shouldThrowWhenJobDoesNotExist() {
            UUID jobId = UUID.randomUUID();

            when(processingJobRepository.findById(jobId))
                    .thenReturn(Optional.empty());

            assertThrows(
                    ResourceNotFoundException.class,
                    () -> processingJobService.completeJob(jobId)
            );

            verify(processingJobRepository, never()).save(any());
            verifyNoInteractions(documentProcessingHandler);
        }

        @Test
        @DisplayName("Should reject a job that is not running")
        void shouldRejectJobThatIsNotRunning() {
            ProcessingJob job = createRunningJob();
            job.setStatus(ProcessingJobStatus.PENDING);

            when(processingJobRepository.findById(any()))
                    .thenReturn(Optional.of(job));

            assertThrows(
                    ConflictException.class,
                    () -> processingJobService.completeJob(UUID.randomUUID())
            );

            verify(processingJobRepository, never()).save(any());
            verifyNoInteractions(documentProcessingHandler);
        }

        @Test
        @DisplayName("Should throw when no handler supports the document type")
        void shouldThrowWhenNoHandlerSupportsDocumentType() {
            ProcessingJob job = createRunningJob();

            when(processingJobRepository.findById(any()))
                    .thenReturn(Optional.of(job));
            when(documentProcessingHandler.supports(DocumentType.RESUME))
                    .thenReturn(false);

            assertThrows(
                    IllegalStateException.class,
                    () -> processingJobService.completeJob(UUID.randomUUID())
            );

            verify(processingJobRepository, never()).save(any());
            verify(documentProcessingHandler, never()).updateStatus(any(), any());
        }
    }

    @Nested
    @DisplayName("failJob()")
    class FailJobTests {

        private ProcessingJob createRunningJob() {
            return ProcessingJob.builder()
                    .documentType(DocumentType.RESUME)
                    .documentId(UUID.randomUUID())
                    .status(ProcessingJobStatus.RUNNING)
                    .currentStage(ProcessingStage.TEXT_EXTRACTION)
                    .progress(50)
                    .retryCount(0)
                    .build();
        }

        @Test
        @DisplayName("Should fail a running job")
        void shouldFailRunningJob() {
            ProcessingJob job = createRunningJob();
            String errorMessage = "Text extraction failed";

            when(processingJobRepository.findById(any()))
                    .thenReturn(Optional.of(job));
            when(documentProcessingHandler.supports(DocumentType.RESUME))
                    .thenReturn(true);
            when(processingJobRepository.save(any()))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            ProcessingJob result = processingJobService.failJob(
                    UUID.randomUUID(), errorMessage
            );

            assertAll(
                    () -> assertEquals(ProcessingJobStatus.FAILED, result.getStatus()),
                    () -> assertNotNull(result.getFinishedAt()),
                    () -> assertEquals(errorMessage, result.getErrorMessage())
            );

            verify(documentProcessingHandler).updateStatus(
                    job.getDocumentId(),
                    DocumentProcessingStatus.FAILED
            );
            verify(processingJobRepository).save(job);
        }

        @Test
        @DisplayName("Should throw when job does not exist")
        void shouldThrowWhenJobDoesNotExist() {
            UUID jobId = UUID.randomUUID();

            when(processingJobRepository.findById(jobId))
                    .thenReturn(Optional.empty());

            assertThrows(
                    ResourceNotFoundException.class,
                    () -> processingJobService.failJob(jobId, "Processing failed")
            );

            verify(processingJobRepository, never()).save(any());
            verifyNoInteractions(documentProcessingHandler);
        }

        @Test
        @DisplayName("Should reject a job that is not running")
        void shouldRejectJobThatIsNotRunning() {
            ProcessingJob job = createRunningJob();
            job.setStatus(ProcessingJobStatus.PENDING);

            when(processingJobRepository.findById(any()))
                    .thenReturn(Optional.of(job));

            assertThrows(
                    ConflictException.class,
                    () -> processingJobService.failJob(UUID.randomUUID(), "Processing failed")
            );

            verify(processingJobRepository, never()).save(any());
            verifyNoInteractions(documentProcessingHandler);
        }

        @Test
        @DisplayName("Should reject a null error message")
        void shouldRejectNullErrorMessage() {
            ProcessingJob job = createRunningJob();

            when(processingJobRepository.findById(any()))
                    .thenReturn(Optional.of(job));

            assertThrows(
                    BadRequestException.class,
                    () -> processingJobService.failJob(UUID.randomUUID(), null)
            );

            verify(processingJobRepository, never()).save(any());
            verifyNoInteractions(documentProcessingHandler);
        }

        @Test
        @DisplayName("Should reject a blank error message")
        void shouldRejectBlankErrorMessage() {
            ProcessingJob job = createRunningJob();

            when(processingJobRepository.findById(any()))
                    .thenReturn(Optional.of(job));

            assertThrows(
                    BadRequestException.class,
                    () -> processingJobService.failJob(UUID.randomUUID(), "   ")
            );

            verify(processingJobRepository, never()).save(any());
            verifyNoInteractions(documentProcessingHandler);
        }

        @Test
        @DisplayName("Should throw when no handler supports the document type")
        void shouldThrowWhenNoHandlerSupportsDocumentType() {
            ProcessingJob job = createRunningJob();

            when(processingJobRepository.findById(any()))
                    .thenReturn(Optional.of(job));
            when(documentProcessingHandler.supports(DocumentType.RESUME))
                    .thenReturn(false);

            assertThrows(
                    IllegalStateException.class,
                    () -> processingJobService.failJob(UUID.randomUUID(), "Processing failed")
            );

            verify(processingJobRepository, never()).save(any());
            verify(documentProcessingHandler, never()).updateStatus(any(), any());
        }
    }

    @Nested
    @DisplayName("getLatestJob()")
    class GetLatestJobTests {

        @Test
        @DisplayName("Should return the latest processing job for a document")
        void shouldReturnLatestJob() {
            DocumentType documentType = DocumentType.RESUME;
            UUID documentId = UUID.randomUUID();

            ProcessingJob job = ProcessingJob.builder()
                    .documentType(documentType)
                    .documentId(documentId)
                    .status(ProcessingJobStatus.COMPLETED)
                    .currentStage(ProcessingStage.COMPLETED)
                    .progress(100)
                    .retryCount(0)
                    .build();

            when(processingJobRepository
                    .findFirstByDocumentTypeAndDocumentIdOrderByCreatedAtDesc(
                            documentType, documentId
                    ))
                    .thenReturn(Optional.of(job));

            ProcessingJob result = processingJobService.getLatestJob(
                    documentType, documentId
            );

            assertEquals(job, result);

            verify(processingJobRepository)
                    .findFirstByDocumentTypeAndDocumentIdOrderByCreatedAtDesc(
                            documentType, documentId
                    );
        }

        @Test
        @DisplayName("Should throw when no processing job exists for the document")
        void shouldThrowWhenLatestJobDoesNotExist() {
            DocumentType documentType = DocumentType.RESUME;
            UUID documentId = UUID.randomUUID();

            when(processingJobRepository
                    .findFirstByDocumentTypeAndDocumentIdOrderByCreatedAtDesc(
                            documentType, documentId
                    ))
                    .thenReturn(Optional.empty());

            assertThrows(
                    ResourceNotFoundException.class,
                    () -> processingJobService.getLatestJob(documentType, documentId)
            );

            verify(processingJobRepository)
                    .findFirstByDocumentTypeAndDocumentIdOrderByCreatedAtDesc(
                            documentType, documentId
                    );
        }
    }

    @Nested
    @DisplayName("getJob()")
    class GetJobTests {

        @Test
        @DisplayName("Should return a processing job when it exists")
        void shouldReturnProcessingJob() {
            UUID jobId = UUID.randomUUID();

            ProcessingJob job = ProcessingJob.builder()
                    .documentType(DocumentType.RESUME)
                    .documentId(UUID.randomUUID())
                    .status(ProcessingJobStatus.RUNNING)
                    .currentStage(ProcessingStage.TEXT_EXTRACTION)
                    .progress(50)
                    .retryCount(0)
                    .build();

            when(processingJobRepository.findById(jobId))
                    .thenReturn(Optional.of(job));

            ProcessingJob result = processingJobService.getJob(jobId);

            assertEquals(job, result);
            verify(processingJobRepository).findById(jobId);
        }

        @Test
        @DisplayName("Should throw when processing job does not exist")
        void shouldThrowWhenProcessingJobDoesNotExist() {
            UUID jobId = UUID.randomUUID();

            when(processingJobRepository.findById(jobId))
                    .thenReturn(Optional.empty());

            assertThrows(
                    ResourceNotFoundException.class,
                    () -> processingJobService.getJob(jobId)
            );

            verify(processingJobRepository).findById(jobId);
        }
    }

}
