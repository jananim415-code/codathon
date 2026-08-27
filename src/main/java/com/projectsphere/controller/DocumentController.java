package com.projectsphere.controller;

import com.projectsphere.entity.Document;
import com.projectsphere.service.DocumentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @PostMapping("/documents")
    public ResponseEntity<Document> createDocument(@Valid @RequestBody Document document) {
        return ResponseEntity.ok(documentService.createDocument(document));
    }

    @GetMapping("/documents")
    public ResponseEntity<List<Document>> listDocuments() {
        return ResponseEntity.ok(documentService.listDocuments());
    }

    @GetMapping("/projects/{projectId}/documents")
    public ResponseEntity<List<Document>> getProjectDocuments(@PathVariable Long projectId) {
        return ResponseEntity.ok(documentService.getProjectDocuments(projectId));
    }

    @PutMapping("/documents/{id}")
    public ResponseEntity<Document> updateDocument(@PathVariable Long id, @Valid @RequestBody Document document) {
        return ResponseEntity.ok(documentService.updateDocument(id, document));
    }

    @DeleteMapping("/documents/{id}")
    public ResponseEntity<Void> deleteDocument(@PathVariable Long id) {
        documentService.deleteDocument(id);
        return ResponseEntity.noContent().build();
    }
}
