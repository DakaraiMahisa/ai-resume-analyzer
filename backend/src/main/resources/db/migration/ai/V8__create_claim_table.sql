CREATE TABLE claims (
    id BINARY(16) NOT NULL,
    created_at TIMESTAMP(6) NOT NULL,
    updated_at TIMESTAMP(6) NOT NULL,
    version BIGINT NOT NULL,

    source_document_id BINARY(16) NOT NULL,
    source_document_type VARCHAR(30) NOT NULL,

    claim_type VARCHAR(50) NOT NULL,
    original_value TEXT NOT NULL,
    canonical_name VARCHAR(255) NOT NULL,
    priority VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL,

    evidence JSON,

    PRIMARY KEY (id),

    INDEX idx_claim_source_document (
        source_document_id,
        source_document_type
    ),

    INDEX idx_claim_type (claim_type),
    INDEX idx_claim_status (status),
    INDEX idx_claim_canonical_name (canonical_name)
);