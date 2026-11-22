CREATE TABLE candidate_projects (
    id BINARY(16) NOT NULL,
    created_at TIMESTAMP(6) NOT NULL,
    updated_at TIMESTAMP(6) NOT NULL,
    version BIGINT NOT NULL,
    user_id BINARY(16) NOT NULL,

    project_name VARCHAR(255) NOT NULL,
    role VARCHAR(150),
    description TEXT,
    technologies JSON,
    project_url VARCHAR(500),

    start_date DATE,
    end_date DATE,
    currently_working BOOLEAN NOT NULL DEFAULT FALSE,

    PRIMARY KEY (id),

    INDEX idx_candidate_projects_user (user_id),

    CONSTRAINT fk_candidate_projects_user
        FOREIGN KEY (user_id)
            REFERENCES users (id)
            ON DELETE CASCADE,

    CONSTRAINT chk_candidate_projects_dates
        CHECK (
            start_date IS NULL
                OR end_date IS NULL
                OR end_date >= start_date
            ),

    CONSTRAINT chk_candidate_projects_current
        CHECK (
            currently_working = FALSE
                OR end_date IS NULL
            )
);