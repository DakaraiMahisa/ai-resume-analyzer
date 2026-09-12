CREATE TABLE analyses (
    id BINARY(16) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    version BIGINT NOT NULL,

    resume_id BINARY(16) NOT NULL,
    job_description_id BINARY(16) NOT NULL,

    status VARCHAR(20) NOT NULL,

    overall_score DOUBLE NOT NULL,
    required_score DOUBLE NOT NULL,
    preferred_score DOUBLE NOT NULL,

    result_data JSON,
    completed_at TIMESTAMP NULL,

    CONSTRAINT pk_analyses
        PRIMARY KEY (id),

    CONSTRAINT fk_analyses_resume
        FOREIGN KEY (resume_id)
        REFERENCES resumes (id),

    CONSTRAINT fk_analyses_job_description
        FOREIGN KEY (job_description_id)
        REFERENCES job_descriptions (id)
);

CREATE INDEX idx_analyses_resume
    ON analyses (resume_id);

CREATE INDEX idx_analyses_job_description
    ON analyses (job_description_id);

CREATE INDEX idx_analyses_status
    ON analyses (status);

CREATE INDEX idx_analyses_created_at
    ON analyses (created_at);

