CREATE TABLE candidate_skills (
    id BINARY(16) NOT NULL,
    created_at TIMESTAMP(6) NOT NULL,
    updated_at TIMESTAMP(6) NOT NULL,
    version BIGINT NOT NULL,
    user_id BINARY(16) NOT NULL,
    skill_name VARCHAR(150) NOT NULL,
    category VARCHAR(30) NOT NULL,
    proficiency_level VARCHAR(30) NOT NULL,
    years_of_experience DECIMAL(4,1),

    PRIMARY KEY (id),

    INDEX idx_candidate_skills_user (user_id),

    CONSTRAINT fk_candidate_skills_user
        FOREIGN KEY (user_id)
            REFERENCES users (id)
            ON DELETE CASCADE,

    CONSTRAINT chk_candidate_skills_experience
        CHECK (
            years_of_experience IS NULL
                OR years_of_experience BETWEEN 0 AND 99.9
            )
);