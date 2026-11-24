package fr.adriencaubel.rag_with_spring.controller;

import fr.adriencaubel.rag_with_spring.model.SourceDocument;
import fr.adriencaubel.rag_with_spring.service.StorageService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/files")
public class DocumentController {

    private final StorageService storageService;

    public DocumentController(StorageService storageService) {
        this.storageService = storageService;
    }

    @GetMapping
    public ResponseEntity<Page<?>> get(Pageable pageable) {
        Page<SourceDocument> documents = storageService.findAll(pageable);
        return ResponseEntity.ok(documents);
    }

    @PostMapping
    public void handleFileUpload(@RequestParam("file") MultipartFile file) {
        storageService.storeAndEmbed(file);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFile(@PathVariable UUID id) {
        storageService.delete(id);
    }
}
