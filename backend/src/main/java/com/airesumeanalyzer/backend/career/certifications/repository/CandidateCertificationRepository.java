package com.airesumeanalyzer.backend.career.certifications.repository;

import com.airesumeanalyzer.backend.career.certifications.entity.CandidateCertification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CandidateCertificationRepository
        extends JpaRepository<CandidateCertification, UUID> {

    List<CandidateCertification> findAllByUserIdOrderByIssueDateDesc(UUID userId);

    Optional<CandidateCertification> findByIdAndUserId(
            UUID id,
            UUID userId
    );
}