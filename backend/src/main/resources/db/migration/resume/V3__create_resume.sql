CREATE TABLE resumes (
    id BINARY(16) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    version BIGINT NOT NULL,

    owner_id BINARY(16) NOT NULL,
    original_filename VARCHAR(255) NOT NULL,
    storage_path VARCHAR(500) NOT NULL,
    raw_text LONGTEXT,
    checksum CHAR(64) NOT NULL,
    processing_status VARCHAR(30) NOT NULL,

    CONSTRAINT pk_resumes
        PRIMARY KEY (id),

    CONSTRAINT fk_resumes_owner
        FOREIGN KEY (owner_id)
        REFERENCES users (id)
        ON DELETE CASCADE,

    CONSTRAINT uk_resumes_owner_checksum
        UNIQUE (owner_id, checksum)
);

CREATE INDEX idx_resume_owner
    ON resumes (owner_id);