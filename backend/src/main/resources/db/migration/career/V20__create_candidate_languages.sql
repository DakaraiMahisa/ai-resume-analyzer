CREATE TABLE candidate_languages (
    id BINARY(16) NOT NULL,
    created_at TIMESTAMP(6) NOT NULL,
    updated_at TIMESTAMP(6) NOT NULL,
    version BIGINT NOT NULL,
    user_id BINARY(16) NOT NULL,

    language_name VARCHAR(100) NOT NULL,
    speaking_level VARCHAR(2) NOT NULL,
    listening_level VARCHAR(2) NOT NULL,
    reading_level VARCHAR(2) NOT NULL,
    writing_level VARCHAR(2) NOT NULL,
    is_native BOOLEAN NOT NULL DEFAULT FALSE,

    PRIMARY KEY (id),

    INDEX idx_candidate_languages_user (user_id),

    CONSTRAINT fk_candidate_languages_user
        FOREIGN KEY (user_id)
            REFERENCES users (id)
            ON DELETE CASCADE,

    CONSTRAINT chk_language_speaking_level
        CHECK (speaking_level IN ('A1', 'A2', 'B1', 'B2', 'C1', 'C2')),

    CONSTRAINT chk_language_listening_level
        CHECK (listening_level IN ('A1', 'A2', 'B1', 'B2', 'C1', 'C2')),

    CONSTRAINT chk_language_reading_level
        CHECK (reading_level IN ('A1', 'A2', 'B1', 'B2', 'C1', 'C2')),

    CONSTRAINT chk_language_writing_level
        CHECK (writing_level IN ('A1', 'A2', 'B1', 'B2', 'C1', 'C2'))
);