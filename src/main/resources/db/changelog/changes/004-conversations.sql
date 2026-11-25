--liquibase formatted sql

--changeset adriencaubel:004-create-table-conversations
CREATE TABLE conversations (
    id          UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
    owner_id    VARCHAR(255)  NOT NULL,
    title       VARCHAR(255)  NOT NULL,
    created_at  TIMESTAMPTZ   NOT NULL DEFAULT now(),
    updated_at  TIMESTAMPTZ   NOT NULL DEFAULT now()
);
--rollback DROP TABLE conversations;

--changeset adriencaubel:004-index-conversations-owner
CREATE INDEX idx_conversations_owner ON conversations (owner_id, updated_at DESC);
--rollback DROP INDEX idx_conversations_owner;

-- Mirrors Spring AI 2.0.1 schema-postgresql.sql (sequence_id is required by
-- PostgresChatMemoryRepositoryDialect). Linked to conversations by
-- conversation_id = conversations.id::text, without a foreign key.
--changeset adriencaubel:004-create-table-spring-ai-chat-memory
CREATE TABLE spring_ai_chat_memory (
    conversation_id  VARCHAR(36)  NOT NULL,
    content          TEXT         NOT NULL,
    type             VARCHAR(10)  NOT NULL CHECK (type IN ('USER', 'ASSISTANT', 'SYSTEM', 'TOOL')),
    "timestamp"      TIMESTAMP    NOT NULL,
    sequence_id      BIGINT       NOT NULL
);
--rollback DROP TABLE spring_ai_chat_memory;

--changeset adriencaubel:004-index-spring-ai-chat-memory
CREATE INDEX spring_ai_chat_memory_conversation_id_timestamp_idx
    ON spring_ai_chat_memory (conversation_id, "timestamp");
CREATE INDEX spring_ai_chat_memory_conversation_id_sequence_id_idx
    ON spring_ai_chat_memory (conversation_id, sequence_id);
--rollback DROP INDEX spring_ai_chat_memory_conversation_id_sequence_id_idx;
--rollback DROP INDEX spring_ai_chat_memory_conversation_id_timestamp_idx;
