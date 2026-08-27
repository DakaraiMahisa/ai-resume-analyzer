CREATE TABLE job_descriptions (
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

    CONSTRAINT pk_job_descriptions
        PRIMARY KEY (id),

    CONSTRAINT fk_job_descriptions_owner
        FOREIGN KEY (owner_id)
        REFERENCES users (id),

    CONSTRAINT uk_job_descriptions_owner_checksum
        UNIQUE (owner_id, checksum)
);

CREATE INDEX idx_job_description_owner
    ON job_descriptions (owner_id);