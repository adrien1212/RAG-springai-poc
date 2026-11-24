package fr.adriencaubel.rag_with_spring.service;

import fr.adriencaubel.rag_with_spring.model.DocumentStatus;
import fr.adriencaubel.rag_with_spring.model.SourceDocument;
import fr.adriencaubel.rag_with_spring.repository.DocumentRepository;
import org.springframework.ai.document.Document;
import org.springframework.ai.document.DocumentTransformer;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class StorageService {
    private final DocumentRepository documentRepository;

    private final VectorStore vectorStore;

    private final JdbcClient jdbc;

    @Value("${spring.ai.ollama.embedding.model}")
    private String embaddingModel;

    public StorageService(DocumentRepository documentRepository, VectorStore vectorStore, JdbcClient jdbc) {
        this.documentRepository = documentRepository;
        this.vectorStore = vectorStore;
        this.jdbc = jdbc;
    }

    @Transactional(readOnly = true)
    public Page<SourceDocument> findAll(Pageable pageable) {
        return documentRepository.findAll(pageable);
    }

    @Transactional
    public void storeAndEmbed(MultipartFile multipartFile) {
        String sha256 = null;
        try {
            sha256 = sha256(multipartFile.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Optional<SourceDocument> existing = documentRepository.findByContentHash(sha256);
        if(existing.isPresent())
            return;

        SourceDocument sourceDocument = documentRepository.save(new SourceDocument(multipartFile.getOriginalFilename(), multipartFile.getContentType(), multipartFile.getSize(), sha256, "toto"));
        sourceDocument.markIndexing();

        try {
            DocumentTransformer splitter = TokenTextSplitter.builder().withChunkSize(400).withMinChunkSizeChars(200).build();
            List<Document> chunks = splitter.apply(new TikaDocumentReader(multipartFile.getResource()).read());
            chunks.forEach(c -> {
                c.getMetadata().put("document_id", sourceDocument.getId());
            });
            vectorStore.add(chunks);
            sourceDocument.markIndexed(chunks.size(), embaddingModel);
            documentRepository.save(sourceDocument);
        } catch (Exception e) {

        }
        return;
    }

    public String sha256(byte[] file) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(file));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public void delete(UUID documentId) {
        SourceDocument document = documentRepository.findById(documentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Document " + documentId + " not found"));

        if (document.getStatus() == DocumentStatus.INDEXING) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Document " + documentId + " is being indexed, retry later");
        }

        // 1. Chunks first (JDBC runs immediately, in the same transaction)
        jdbc.sql("DELETE FROM vector_store WHERE metadata->>'document_id' = ?")
                .param(documentId.toString())
                .update();

        // 2. Document row (flushed on commit)
        documentRepository.delete(document);
    }


}
