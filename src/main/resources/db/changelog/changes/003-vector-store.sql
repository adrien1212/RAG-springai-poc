--liquibase formatted sql

--changeset adriencaubel:003-create-table-vector-store
CREATE TABLE vector_store (
    id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    content    TEXT,
    metadata   JSONB,
    embedding  VECTOR(1024)
);
--rollback DROP TABLE vector_store;

--changeset adriencaubel:003-index-vector-store-embedding
CREATE INDEX idx_vector_store_embedding
    ON vector_store USING hnsw (embedding vector_cosine_ops);
--rollback DROP INDEX idx_vector_store_embedding;

--changeset adriencaubel:003-index-vector-store-metadata
CREATE INDEX idx_vector_store_metadata
    ON vector_store USING gin (metadata jsonb_path_ops);
--rollback DROP INDEX idx_vector_store_metadata;
