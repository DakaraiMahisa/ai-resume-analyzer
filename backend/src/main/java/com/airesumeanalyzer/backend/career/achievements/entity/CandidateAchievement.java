package com.airesumeanalyzer.backend.career.achievements.entity;

import com.airesumeanalyzer.backend.career.achievements.enums.AchievementType;
import com.airesumeanalyzer.backend.common.entity.BaseEntity;
import com.airesumeanalyzer.backend.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Entity
@Table(
        name = "candidate_achievements",
        indexes = {
                @Index(
                        name = "idx_candidate_achievements_user",
                        columnList = "user_id"
                )
        }
)
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CandidateAchievement extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "user_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_candidate_achievements_user")
    )
    private User user;

    @Column(name = "achievement_name", nullable = false, length = 255)
    private String achievementName;

    @Column(name = "issuing_organization", length = 255)
    private String issuingOrganization;

    @Enumerated(EnumType.STRING)
    @Column(name = "achievement_type", nullable = false, length = 30)
    private AchievementType achievementType;

    @Column(name = "issue_date")
    private LocalDate issueDate;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "achievement_url", length = 500)
    private String achievementUrl;
}
