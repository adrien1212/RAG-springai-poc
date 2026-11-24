--liquibase formatted sql

--changeset adriencaubel:002-create-table-documents
CREATE TABLE documents (
    id               UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
    filename         VARCHAR(512)  NOT NULL,
    mime_type        VARCHAR(255),
    size_bytes       BIGINT        NOT NULL,
    content_hash     VARCHAR(64)      NOT NULL,
    storage_path     VARCHAR(1024) NOT NULL,
    status           VARCHAR(20)   NOT NULL DEFAULT 'PENDING',
    error_message    TEXT,
    chunk_count      INTEGER,
    embedding_model  VARCHAR(100),
    created_at       TIMESTAMPTZ   NOT NULL DEFAULT now(),
    indexed_at       TIMESTAMPTZ,

    CONSTRAINT uq_documents_hash UNIQUE (content_hash),
    CONSTRAINT ck_documents_status
        CHECK (status IN ('PENDING', 'INDEXING', 'INDEXED', 'FAILED'))
);
--rollback DROP TABLE documents;

--changeset adriencaubel:002-index-documents-status
CREATE INDEX idx_documents_status ON documents (status);
--rollback DROP INDEX idx_documents_status;
