package com.airesumeanalyzer.backend.career.skills.repository;

import com.airesumeanalyzer.backend.career.skills.entity.CandidateSkill;
import com.airesumeanalyzer.backend.career.skills.enums.SkillCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CandidateSkillRepository extends JpaRepository<CandidateSkill, UUID> {

    List<CandidateSkill> findAllByUserIdOrderBySkillNameAsc(UUID userId);

    Optional<CandidateSkill> findByIdAndUserId(UUID id, UUID userId);

    boolean existsByUserIdAndCategoryAndSkillNameIgnoreCase(
            UUID userId,
            SkillCategory category,
            String skillName
    );

    boolean existsByUserIdAndCategoryAndSkillNameIgnoreCaseAndIdNot(
            UUID userId,
            SkillCategory category,
            String skillName,
            UUID id
    );
}