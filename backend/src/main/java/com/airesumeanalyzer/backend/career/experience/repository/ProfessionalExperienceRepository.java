package com.airesumeanalyzer.backend.career.experience.repository;


import com.airesumeanalyzer.backend.career.experience.entity.ProfessionalExperience;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProfessionalExperienceRepository
        extends JpaRepository<ProfessionalExperience, UUID> {

    List<ProfessionalExperience> findAllByUserIdOrderByStartDateDesc(UUID userId);

    Optional<ProfessionalExperience> findByIdAndUserId(UUID id, UUID userId);

}