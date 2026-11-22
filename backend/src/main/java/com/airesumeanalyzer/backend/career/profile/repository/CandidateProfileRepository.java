package com.airesumeanalyzer.backend.career.profile.repository;

import com.airesumeanalyzer.backend.career.profile.entity.CandidateProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CandidateProfileRepository
        extends JpaRepository<CandidateProfile, UUID> {

    Optional<CandidateProfile> findByUserId(UUID userId);

    boolean existsByUserId(UUID userId);
}