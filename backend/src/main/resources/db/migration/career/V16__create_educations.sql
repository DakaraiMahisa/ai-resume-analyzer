CREATE TABLE educations (
    id BINARY(16) NOT NULL,
    created_at TIMESTAMP(6) NOT NULL,
    updated_at TIMESTAMP(6) NOT NULL,
    version BIGINT NOT NULL,

    user_id BINARY(16) NOT NULL,

    institution VARCHAR(255) NOT NULL,
    degree VARCHAR(150) NOT NULL,
    field_of_study VARCHAR(255),
    location VARCHAR(255),

    start_date DATE NOT NULL,
    end_date DATE,
    currently_studying BOOLEAN NOT NULL DEFAULT FALSE,

    grade VARCHAR(100),
    description TEXT,

    PRIMARY KEY (id),

    INDEX idx_educations_user (user_id),

    CONSTRAINT fk_educations_user
        FOREIGN KEY (user_id)
            REFERENCES users (id)
            ON DELETE CASCADE,

    CONSTRAINT chk_education_dates
        CHECK (
            end_date IS NULL OR end_date >= start_date
            ),

    CONSTRAINT chk_education_current
        CHECK (
            currently_studying = FALSE OR end_date IS NULL
            )
);