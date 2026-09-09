package com.airesumeanalyzer.backend.processing.repository;

import com.airesumeanalyzer.backend.processing.entity.ProcessingJob;
import com.airesumeanalyzer.backend.processing.enums.DocumentType;
import com.airesumeanalyzer.backend.processing.enums.ProcessingJobStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProcessingJobRepository extends JpaRepository<ProcessingJob, UUID> {

    List<ProcessingJob> findByDocumentTypeAndDocumentId(
            DocumentType documentType,
            UUID documentId
    );

    Optional<ProcessingJob> findFirstByDocumentTypeAndDocumentIdOrderByCreatedAtDesc(
            DocumentType documentType,
            UUID documentId
    );

    List<ProcessingJob> findByStatusOrderByCreatedAtAsc(
            ProcessingJobStatus status
    );
}