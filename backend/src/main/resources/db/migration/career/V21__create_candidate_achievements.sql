CREATE TABLE candidate_achievements (
    id BINARY(16) NOT NULL,
    created_at TIMESTAMP(6) NOT NULL,
    updated_at TIMESTAMP(6) NOT NULL,
    version BIGINT NOT NULL,
    user_id BINARY(16) NOT NULL,

    achievement_name VARCHAR(255) NOT NULL,
    issuing_organization VARCHAR(255),
    achievement_type VARCHAR(30) NOT NULL,
    issue_date DATE,
    description TEXT,
    achievement_url VARCHAR(500),

    PRIMARY KEY (id),

    INDEX idx_candidate_achievements_user (user_id),

    CONSTRAINT fk_candidate_achievements_user
        FOREIGN KEY (user_id)
            REFERENCES users (id)
            ON DELETE CASCADE,

    CONSTRAINT chk_candidate_achievements_type
        CHECK (
            achievement_type IN (
                                 'AWARD',
                                 'HONOR',
                                 'COMPETITION',
                                 'SCHOLARSHIP',
                                 'RECOGNITION',
                                 'OTHER'
                )
            )
);