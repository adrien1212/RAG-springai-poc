--liquibase formatted sql

--changeset adriencaubel:001-create-extension-vector
CREATE EXTENSION IF NOT EXISTS vector;
--rollback DROP EXTENSION IF EXISTS vector;

--changeset adriencaubel:001-create-schema-embedding-db
CREATE SCHEMA IF NOT EXISTS embedding_db;
--rollback DROP SCHEMA IF EXISTS embedding_db;
