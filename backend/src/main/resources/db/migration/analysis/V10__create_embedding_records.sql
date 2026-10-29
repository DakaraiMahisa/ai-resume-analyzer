CREATE TABLE embedding_records (
    id BINARY(16) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    version BIGINT NOT NULL,

    model VARCHAR(100) NOT NULL,
    input_hash VARCHAR(64) NOT NULL,
    input_text TEXT NOT NULL,
    dimensions INT NOT NULL,
    vector BLOB NOT NULL,

    CONSTRAINT pk_embedding_records
        PRIMARY KEY (id),

    CONSTRAINT uk_embedding_records_model_hash
        UNIQUE (model, input_hash)
);