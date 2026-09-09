CREATE TABLE processing_jobs (
    id BINARY(16) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    version BIGINT NOT NULL,

    document_type VARCHAR(30) NOT NULL,
    document_id BINARY(16) NOT NULL,

    status VARCHAR(30) NOT NULL,
    current_stage VARCHAR(30) NOT NULL,

    progress INT NOT NULL,
    retry_count INT NOT NULL,

    started_at TIMESTAMP NULL,
    finished_at TIMESTAMP NULL,

    error_message LONGTEXT,

    CONSTRAINT pk_processing_jobs
        PRIMARY KEY (id)
);

CREATE INDEX idx_processing_job_document
    ON processing_jobs (document_type, document_id);

CREATE INDEX idx_processing_job_status
    ON processing_jobs (status);