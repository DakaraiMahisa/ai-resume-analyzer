package com.airesumeanalyzer.backend.career.languages.entity;


import com.airesumeanalyzer.backend.career.languages.enums.LanguageProficiency;
import com.airesumeanalyzer.backend.common.entity.BaseEntity;
import com.airesumeanalyzer.backend.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(
        name = "candidate_languages",
        indexes = {
                @Index(
                        name = "idx_candidate_languages_user",
                        columnList = "user_id"
                )
        }
)
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CandidateLanguage extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "user_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_candidate_languages_user")
    )
    private User user;

    @Column(name = "language_name", nullable = false, length = 100)
    private String languageName;

    @Enumerated(EnumType.STRING)
    @Column(name = "speaking_level", nullable = false, length = 2)
    private LanguageProficiency speakingLevel;

    @Enumerated(EnumType.STRING)
    @Column(name = "listening_level", nullable = false, length = 2)
    private LanguageProficiency listeningLevel;

    @Enumerated(EnumType.STRING)
    @Column(name = "reading_level", nullable = false, length = 2)
    private LanguageProficiency readingLevel;

    @Enumerated(EnumType.STRING)
    @Column(name = "writing_level", nullable = false, length = 2)
    private LanguageProficiency writingLevel;

    @Column(name = "is_native", nullable = false)
    @Builder.Default
    private boolean nativeLanguage = false;
}
