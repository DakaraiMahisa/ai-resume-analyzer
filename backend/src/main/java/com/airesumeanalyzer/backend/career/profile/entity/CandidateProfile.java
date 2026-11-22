package com.airesumeanalyzer.backend.career.profile.entity;


import com.airesumeanalyzer.backend.common.entity.BaseEntity;
import com.airesumeanalyzer.backend.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;


@Entity
@Table(
        name = "candidate_profiles",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_candidate_profiles_user",
                        columnNames = "user_id"
                )
        }
)
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CandidateProfile extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "user_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_candidate_profiles_user")
    )
    private User user;

    @Column(name = "professional_headline", length = 255)
    private String professionalHeadline;

    @Column(name = "professional_summary", columnDefinition = "TEXT")
    private String professionalSummary;

    @Column(length = 30)
    private String phone;

    @Column(length = 255)
    private String location;

    @Column(name = "linkedin_url", length = 500)
    private String linkedinUrl;

    @Column(name = "github_url", length = 500)
    private String githubUrl;

    @Column(name = "portfolio_url", length = 500)
    private String portfolioUrl;

    @Column(name = "career_objective", columnDefinition = "TEXT")
    private String careerObjective;
}

