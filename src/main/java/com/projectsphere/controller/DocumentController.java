package com.projectsphere.controller;

import com.projectsphere.dto.DocumentRequest;
import com.projectsphere.dto.DocumentResponse;
import com.projectsphere.dto.EntityDtoMapper;
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
    public ResponseEntity<DocumentResponse> createDocument(@Valid @RequestBody DocumentRequest request) {
        return ResponseEntity.ok(EntityDtoMapper.toResponse(documentService.createDocument(EntityDtoMapper.toEntity(request))));
    }

    @GetMapping("/documents")
    public ResponseEntity<List<DocumentResponse>> listDocuments() {
        return ResponseEntity.ok(documentService.listDocuments().stream().map(EntityDtoMapper::toResponse).toList());
    }

    @GetMapping("/projects/{projectId}/documents")
    public ResponseEntity<List<DocumentResponse>> getProjectDocuments(@PathVariable Long projectId) {
        return ResponseEntity.ok(documentService.getProjectDocuments(projectId).stream().map(EntityDtoMapper::toResponse).toList());
    }

    @PutMapping("/documents/{id}")
    public ResponseEntity<DocumentResponse> updateDocument(@PathVariable Long id, @Valid @RequestBody DocumentRequest request) {
        return ResponseEntity.ok(EntityDtoMapper.toResponse(documentService.updateDocument(id, EntityDtoMapper.toEntity(request))));
    }

    @DeleteMapping("/documents/{id}")
    public ResponseEntity<Void> deleteDocument(@PathVariable Long id) {
        documentService.deleteDocument(id);
        return ResponseEntity.noContent().build();
    }
}
