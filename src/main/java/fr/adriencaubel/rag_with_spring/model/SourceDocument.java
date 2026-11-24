package fr.adriencaubel.rag_with_spring.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Uploaded source file. Its chunks live in embedding_db.vector_store,
 * linked through metadata.document_id.
 */
@Entity
@Table(name = "documents")
public class SourceDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 512)
    private String filename;

    @Column(name = "mime_type")
    private String mimeType;

    @Column(name = "size_bytes", nullable = false)
    private long sizeBytes;

    @Column(name = "content_hash", nullable = false, unique = true)
    private String contentHash;

    @Column(name = "storage_path", nullable = false, length = 1024)
    private String storagePath;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private DocumentStatus status = DocumentStatus.PENDING;

    @Column(name = "error_message", columnDefinition = "text")
    private String errorMessage;

    @Column(name = "chunk_count")
    private Integer chunkCount;

    @Column(name = "embedding_model", length = 100)
    private String embeddingModel;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "indexed_at")
    private OffsetDateTime indexedAt;

    protected SourceDocument() {
        // for JPA
    }

    public SourceDocument(String filename, String mimeType, long sizeBytes,
                          String contentHash, String storagePath) {
        this.filename = filename;
        this.mimeType = mimeType;
        this.sizeBytes = sizeBytes;
        this.contentHash = contentHash;
        this.storagePath = storagePath;
    }

    // --- state transitions -------------------------------------------

    public void markIndexing() {
        this.status = DocumentStatus.INDEXING;
        this.errorMessage = null;
    }

    public void markIndexed(int chunkCount, String embeddingModel) {
        this.status = DocumentStatus.INDEXED;
        this.chunkCount = chunkCount;
        this.embeddingModel = embeddingModel;
        this.indexedAt = OffsetDateTime.now();
    }

    public void markFailed(String errorMessage) {
        this.status = DocumentStatus.FAILED;
        this.errorMessage = errorMessage;
    }

    // --- getters -----------------------------------------------------

    public UUID getId() { return id; }
    public String getFilename() { return filename; }
    public String getMimeType() { return mimeType; }
    public long getSizeBytes() { return sizeBytes; }
    public String getContentHash() { return contentHash; }
    public String getStoragePath() { return storagePath; }
    public DocumentStatus getStatus() { return status; }
    public String getErrorMessage() { return errorMessage; }
    public Integer getChunkCount() { return chunkCount; }
    public String getEmbeddingModel() { return embeddingModel; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public OffsetDateTime getIndexedAt() { return indexedAt; }
}
