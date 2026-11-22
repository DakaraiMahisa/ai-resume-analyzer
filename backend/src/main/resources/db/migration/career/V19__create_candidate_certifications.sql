CREATE TABLE candidate_certifications (
    id BINARY(16) NOT NULL,
    created_at TIMESTAMP(6) NOT NULL,
    updated_at TIMESTAMP(6) NOT NULL,
    version BIGINT NOT NULL,
    user_id BINARY(16) NOT NULL,

    certification_name VARCHAR(255) NOT NULL,
    issuing_organization VARCHAR(255) NOT NULL,
    issue_date DATE,
    expiration_date DATE,
    credential_id VARCHAR(255),
    credential_url VARCHAR(500),
    does_not_expire BOOLEAN NOT NULL DEFAULT FALSE,

    PRIMARY KEY (id),

    INDEX idx_candidate_certifications_user (user_id),

    CONSTRAINT fk_candidate_certifications_user
        FOREIGN KEY (user_id)
            REFERENCES users (id)
            ON DELETE CASCADE,

    CONSTRAINT chk_certification_dates
        CHECK (
            issue_date IS NULL
                OR expiration_date IS NULL
                OR expiration_date >= issue_date
            ),

    CONSTRAINT chk_certification_no_expiration
        CHECK (
            does_not_expire = FALSE
                OR expiration_date IS NULL
            )
);