package com.airesumeanalyzer.backend.career.education.repository;

import com.airesumeanalyzer.backend.career.education.entity.Education;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EducationRepository extends JpaRepository<Education, UUID> {

    List<Education> findAllByUserIdOrderByStartDateDesc(UUID userId);

    Optional<Education> findByIdAndUserId(UUID id, UUID userId);
}