package com.airesumeanalyzer.backend.embedding.repository;

import com.airesumeanalyzer.backend.embedding.entity.EmbeddingRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;
import java.util.Collection;
import java.util.List;

public interface EmbeddingRecordRepository
        extends JpaRepository<EmbeddingRecord, UUID> {

    List<EmbeddingRecord> findAllByModelAndInputHashIn(
            String model,
            Collection<String> inputHashes
    );
}