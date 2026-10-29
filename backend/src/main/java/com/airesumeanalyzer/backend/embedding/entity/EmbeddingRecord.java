package com.airesumeanalyzer.backend.embedding.entity;

import com.airesumeanalyzer.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;


@Entity
@Table(
        name = "embedding_records",
        indexes = {
                @Index(
                        name = "idx_embedding_records_model_hash",
                        columnList = "model,input_hash",
                        unique = true
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class EmbeddingRecord extends BaseEntity {

    @Column(nullable = false, length = 100)
    private String model;

    @Column(name = "input_hash", nullable = false, length = 64)
    private String inputHash;

    @Column(name = "input_text", nullable = false, columnDefinition = "TEXT")
    private String inputText;

    @Column(nullable = false)
    private Integer dimensions;

    @Column(nullable = false, columnDefinition = "BLOB")
    private byte[] vector;
}
