package com.airesumeanalyzer.backend.career.skills.entity;

import com.airesumeanalyzer.backend.career.skills.enums.ProficiencyLevel;
import com.airesumeanalyzer.backend.career.skills.enums.SkillCategory;
import com.airesumeanalyzer.backend.common.entity.BaseEntity;
import com.airesumeanalyzer.backend.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Entity
@Table(
        name = "candidate_skills",
        indexes = {
                @Index(name = "idx_candidate_skills_user", columnList = "user_id")
        }
)
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CandidateSkill extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "user_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_candidate_skills_user")
    )
    private User user;

    @Column(name = "skill_name", nullable = false, length = 150)
    private String skillName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private SkillCategory category;

    @Enumerated(EnumType.STRING)
    @Column(name = "proficiency_level", nullable = false, length = 30)
    private ProficiencyLevel proficiencyLevel;

    @Column(name = "years_of_experience", precision = 4, scale = 1)
    private BigDecimal yearsOfExperience;
}