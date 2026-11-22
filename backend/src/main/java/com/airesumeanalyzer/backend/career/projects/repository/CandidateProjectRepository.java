package com.airesumeanalyzer.backend.career.projects.repository;

import com.airesumeanalyzer.backend.career.projects.entity.CandidateProject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CandidateProjectRepository
        extends JpaRepository<CandidateProject, UUID> {

    List<CandidateProject> findAllByUserIdOrderByCreatedAtDesc(UUID userId);

    Optional<CandidateProject> findByIdAndUserId(UUID id, UUID userId);
}
