package com.projectsphere.service;

import com.projectsphere.entity.Document;
import com.projectsphere.entity.Project;
import com.projectsphere.entity.User;
import com.projectsphere.exception.ResourceNotFoundException;
import com.projectsphere.repository.DocumentRepository;
import com.projectsphere.repository.ProjectRepository;
import com.projectsphere.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public DocumentService(DocumentRepository documentRepository, ProjectRepository projectRepository, UserRepository userRepository) {
        this.documentRepository = documentRepository;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
    }

    public Document createDocument(@Valid Document document) {
        if (document.getProject() != null && document.getProject().getId() != null) {
            Project project = projectRepository.findById(document.getProject().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));
            document.setProject(project);
        }
        if (document.getCreatedBy() != null && document.getCreatedBy().getId() != null) {
            User user = userRepository.findById(document.getCreatedBy().getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
            document.setCreatedBy(user);
        }
        document.setUpdatedAt(LocalDateTime.now());
        return documentRepository.save(document);
    }

    public List<Document> listDocuments() {
        return documentRepository.findAll();
    }

    public List<Document> getProjectDocuments(Long projectId) {
        Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new ResourceNotFoundException("Project not found"));
        return documentRepository.findByProject(project);
    }

    public Document updateDocument(Long id, Document updatedDocument) {
        Document document = getDocument(id);
        if (updatedDocument.getTitle() != null) document.setTitle(updatedDocument.getTitle());
        if (updatedDocument.getContent() != null) document.setContent(updatedDocument.getContent());
        document.setUpdatedAt(LocalDateTime.now());
        return documentRepository.save(document);
    }

    public void deleteDocument(Long id) {
        Document document = getDocument(id);
        documentRepository.delete(document);
    }

    public Document getDocument(Long id) {
        return documentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Document not found"));
    }
}
