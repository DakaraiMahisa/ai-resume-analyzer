CREATE TABLE professional_experiences (
    id BINARY(16) NOT NULL,
    created_at TIMESTAMP(6) NOT NULL,
    updated_at TIMESTAMP(6) NOT NULL,
    version BIGINT NOT NULL,

    user_id BINARY(16) NOT NULL,

    company_name VARCHAR(255) NOT NULL,
    job_title VARCHAR(255) NOT NULL,
    employment_type VARCHAR(30),
    location VARCHAR(255),

    start_date DATE NOT NULL,
    end_date DATE,
    currently_working BOOLEAN NOT NULL DEFAULT FALSE,

    description TEXT,

    PRIMARY KEY (id),

    INDEX idx_professional_experiences_user (user_id),

    CONSTRAINT fk_professional_experiences_user
        FOREIGN KEY (user_id)
            REFERENCES users (id)
            ON DELETE CASCADE,

    CONSTRAINT chk_professional_experience_dates
        CHECK (
            end_date IS NULL OR end_date >= start_date
            ),

    CONSTRAINT chk_professional_experience_current
        CHECK (
            currently_working = FALSE OR end_date IS NULL
            )
);