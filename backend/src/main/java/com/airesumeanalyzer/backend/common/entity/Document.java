package com.airesumeanalyzer.backend.common.entity;

import com.airesumeanalyzer.backend.auth.entity.User;
import com.airesumeanalyzer.backend.common.enums.DocumentProcessingStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@MappedSuperclass
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class Document extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "owner_id",
            nullable = false
    )
    private User owner;

    @Column(
            name = "raw_text",
            columnDefinition = "TEXT"
    )
    private String rawText;

    @Column(
            name = "structured_data",
            columnDefinition = "JSON"
    )
    private String structuredData;

    @Column(
            nullable = false,
            length = 64
    )
    private String checksum;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "processing_status",
            nullable = false,
            length = 30
    )
    @Builder.Default
    private DocumentProcessingStatus processingStatus =
            DocumentProcessingStatus.UPLOADED;
}
