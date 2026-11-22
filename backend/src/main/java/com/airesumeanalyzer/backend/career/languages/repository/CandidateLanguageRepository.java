package com.airesumeanalyzer.backend.career.languages.repository;

import com.airesumeanalyzer.backend.career.languages.entity.CandidateLanguage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CandidateLanguageRepository
        extends JpaRepository<CandidateLanguage, UUID> {

    List<CandidateLanguage> findAllByUserIdOrderByLanguageNameAsc(UUID userId);

    Optional<CandidateLanguage> findByIdAndUserId(UUID id, UUID userId);

    boolean existsByUserIdAndLanguageNameIgnoreCase(UUID userId, String languageName);

    boolean existsByUserIdAndLanguageNameIgnoreCaseAndIdNot(
            UUID userId,
            String languageName,
            UUID id
    );
}
