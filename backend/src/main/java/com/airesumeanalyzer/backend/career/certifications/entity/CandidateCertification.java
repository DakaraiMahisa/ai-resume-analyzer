package com.airesumeanalyzer.backend.career.certifications.entity;

import com.airesumeanalyzer.backend.common.entity.BaseEntity;
import com.airesumeanalyzer.backend.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Entity
@Table(
        name = "candidate_certifications",
        indexes = {
                @Index(
                        name = "idx_candidate_certifications_user",
                        columnList = "user_id"
                )
        }
)
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CandidateCertification extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "user_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_candidate_certifications_user")
    )
    private User user;

    @Column(name = "certification_name", nullable = false, length = 255)
    private String certificationName;

    @Column(name = "issuing_organization", nullable = false, length = 255)
    private String issuingOrganization;

    @Column(name = "issue_date")
    private LocalDate issueDate;

    @Column(name = "expiration_date")
    private LocalDate expirationDate;

    @Column(name = "credential_id", length = 255)
    private String credentialId;

    @Column(name = "credential_url", length = 500)
    private String credentialUrl;

    @Column(name = "does_not_expire", nullable = false)
    @Builder.Default
    private boolean doesNotExpire = false;
}