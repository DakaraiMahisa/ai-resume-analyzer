package com.airesumeanalyzer.backend.career.preferences.repository;



import com.airesumeanalyzer.backend.career.preferences.entity.CandidateCareerPreference;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CandidateCareerPreferenceRepository
        extends JpaRepository<CandidateCareerPreference, UUID> {

    Optional<CandidateCareerPreference> findByUserId(UUID userId);

    boolean existsByUserId(UUID userId);
}
