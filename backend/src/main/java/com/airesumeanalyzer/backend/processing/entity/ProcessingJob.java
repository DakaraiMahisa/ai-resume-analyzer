package com.airesumeanalyzer.backend.processing.entity;

import com.airesumeanalyzer.backend.common.entity.BaseEntity;
import com.airesumeanalyzer.backend.processing.enums.DocumentType;
import com.airesumeanalyzer.backend.processing.enums.ProcessingJobStatus;
import com.airesumeanalyzer.backend.processing.enums.ProcessingStage;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "processing_jobs")
public class ProcessingJob extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(
            name = "document_type",
            nullable = false,
            length = 30
    )
    private DocumentType documentType;

    @Column(
            name = "document_id",
            nullable = false
    )
    private UUID documentId;

    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 30
    )
    @Builder.Default
    private ProcessingJobStatus status =
            ProcessingJobStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "current_stage",
            nullable = false,
            length = 30
    )
    @Builder.Default
    private ProcessingStage currentStage =
            ProcessingStage.INITIALIZATION;

    @Column(
            nullable = false
    )
    @Builder.Default
    private Integer progress = 0;

    @Column(
            name = "retry_count",
            nullable = false
    )
    @Builder.Default
    private Integer retryCount = 0;

    @Column(name = "started_at")
    private Instant startedAt;

    @Column(name = "finished_at")
    private Instant finishedAt;

    @Column(columnDefinition = "TEXT")
    private String errorMessage;
}