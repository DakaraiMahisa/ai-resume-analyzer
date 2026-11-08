ALTER TABLE claims
DROP INDEX idx_claim_canonical_name,
    MODIFY COLUMN canonical_name VARCHAR(1000) NOT NULL,
    ADD INDEX idx_claim_canonical_name (canonical_name(191));