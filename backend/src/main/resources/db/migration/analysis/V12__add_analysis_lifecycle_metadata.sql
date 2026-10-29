ALTER TABLE analyses
    ADD COLUMN started_at TIMESTAMP NULL,
    ADD COLUMN failure_code VARCHAR(50) NULL,
    ADD COLUMN retryable BOOLEAN NOT NULL DEFAULT TRUE,
    ADD COLUMN failure_trace_id VARCHAR(100) NULL,
    ADD COLUMN attempt_number INT NOT NULL DEFAULT 1,
    ADD COLUMN parent_analysis_id BINARY(16) NULL,
    ADD COLUMN quota_reserved BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN quota_consumed BOOLEAN NOT NULL DEFAULT FALSE;

CREATE INDEX idx_analyses_parent
    ON analyses (parent_analysis_id);

CREATE INDEX idx_analyses_quota
    ON analyses (resume_id, status, quota_reserved, quota_consumed);
