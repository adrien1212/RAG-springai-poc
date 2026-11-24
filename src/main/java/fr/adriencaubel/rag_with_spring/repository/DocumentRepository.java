package fr.adriencaubel.rag_with_spring.repository;

import fr.adriencaubel.rag_with_spring.model.SourceDocument;
import fr.adriencaubel.rag_with_spring.model.DocumentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DocumentRepository extends JpaRepository<SourceDocument, UUID> {
    Optional<SourceDocument> findByContentHash(String contentHash);

    List<SourceDocument> findByStatus(DocumentStatus status);
}
