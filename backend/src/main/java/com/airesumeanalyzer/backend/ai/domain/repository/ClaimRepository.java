package com.airesumeanalyzer.backend.ai.domain.repository;

import com.airesumeanalyzer.backend.ai.domain.entity.Claim;
import com.airesumeanalyzer.backend.ai.domain.entity.ClaimStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ClaimRepository extends JpaRepository<Claim, UUID> {

    List<Claim> findBySourceDocumentId(UUID sourceDocumentId);

    List<Claim> findBySourceDocumentIdAndStatus(
            UUID sourceDocumentId,
            ClaimStatus status
    );
}