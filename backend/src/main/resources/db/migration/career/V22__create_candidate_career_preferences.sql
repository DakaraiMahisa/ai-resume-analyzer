CREATE TABLE candidate_career_preferences (
    id BINARY(16) NOT NULL,
    created_at TIMESTAMP(6) NOT NULL,
    updated_at TIMESTAMP(6) NOT NULL,
    version BIGINT NOT NULL,

    user_id BINARY(16) NOT NULL,

    preferred_job_titles JSON,
    preferred_employment_types JSON,
    preferred_work_modes JSON,
    preferred_locations JSON,
    preferred_industries JSON,

    minimum_salary DECIMAL(12,2),
    maximum_salary DECIMAL(12,2),
    salary_currency VARCHAR(3),
    salary_period VARCHAR(20),

    willing_to_relocate BOOLEAN,
    notice_period_days INTEGER,
    job_search_status VARCHAR(30),

    PRIMARY KEY (id),

    CONSTRAINT uk_candidate_career_preferences_user
        UNIQUE (user_id),

    CONSTRAINT fk_candidate_career_preferences_user
        FOREIGN KEY (user_id) REFERENCES users (id)
            ON DELETE CASCADE,

    CONSTRAINT chk_career_preferences_salary_range
        CHECK (
            minimum_salary IS NULL
                OR maximum_salary IS NULL
                OR minimum_salary <= maximum_salary
            ),

    CONSTRAINT chk_career_preferences_salary_non_negative
        CHECK (
            (minimum_salary IS NULL OR minimum_salary >= 0)
                AND (maximum_salary IS NULL OR maximum_salary >= 0)
            ),

    CONSTRAINT chk_career_preferences_notice_period
        CHECK (notice_period_days IS NULL OR notice_period_days >= 0)
);