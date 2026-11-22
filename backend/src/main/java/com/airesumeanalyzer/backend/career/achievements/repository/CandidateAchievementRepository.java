package com.airesumeanalyzer.backend.career.achievements.repository;

import com.airesumeanalyzer.backend.career.achievements.entity.CandidateAchievement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CandidateAchievementRepository
        extends JpaRepository<CandidateAchievement, UUID> {

    List<CandidateAchievement> findAllByUserIdOrderByIssueDateDesc(UUID userId);

    Optional<CandidateAchievement> findByIdAndUserId(
            UUID id,
            UUID userId
    );

    boolean existsByUserIdAndAchievementNameIgnoreCaseAndIssuingOrganizationIgnoreCase(
            UUID userId,
            String achievementName,
            String issuingOrganization
    );

    boolean existsByUserIdAndAchievementNameIgnoreCaseAndIssuingOrganizationIgnoreCaseAndIdNot(
            UUID userId,
            String achievementName,
            String issuingOrganization,
            UUID id
    );

    @Query("""
    SELECT COUNT(a) > 0
    FROM CandidateAchievement a
    WHERE a.user.id = :userId
      AND LOWER(a.achievementName) = LOWER(:achievementName)
      AND (
          (:issuingOrganization IS NULL AND a.issuingOrganization IS NULL)
          OR LOWER(a.issuingOrganization) = LOWER(:issuingOrganization)
      )
""")
    boolean existsDuplicate(
            @Param("userId") UUID userId,
            @Param("achievementName") String achievementName,
            @Param("issuingOrganization") String issuingOrganization
    );

    @Query("""
    SELECT COUNT(a) > 0
    FROM CandidateAchievement a
    WHERE a.user.id = :userId
      AND a.id <> :id
      AND LOWER(a.achievementName) = LOWER(:achievementName)
      AND (
          (:issuingOrganization IS NULL AND a.issuingOrganization IS NULL)
          OR LOWER(a.issuingOrganization) = LOWER(:issuingOrganization)
      )
""")
    boolean existsDuplicateExcludingId(
            @Param("userId") UUID userId,
            @Param("achievementName") String achievementName,
            @Param("issuingOrganization") String issuingOrganization,
            @Param("id") UUID id
    );
}
