package com.airesumeanalyzer.backend.ai.domain.entity;

import com.airesumeanalyzer.backend.ai.model.claims.ClaimPriority;
import com.airesumeanalyzer.backend.ai.model.claims.ClaimType;
import com.airesumeanalyzer.backend.common.entity.BaseEntity;
import com.airesumeanalyzer.backend.common.entity.Document;
import com.airesumeanalyzer.backend.processing.enums.DocumentType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Entity
@Table(name = "claims")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Claim extends BaseEntity {

    @Column(name = "source_document_id", nullable = false)
    private UUID sourceDocumentId;

    @Enumerated(EnumType.STRING)
    @Column(name = "source_document_type", nullable = false, length = 30)
    private DocumentType sourceDocumentType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ClaimType claimType;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String originalValue;

    @Column(nullable = false)
    private String canonicalName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ClaimPriority priority;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ClaimStatus status;

    @Column(columnDefinition = "JSON")
    private String evidence;
}