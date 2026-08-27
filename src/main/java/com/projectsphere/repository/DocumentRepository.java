package com.projectsphere.repository;

import com.projectsphere.entity.Document;
import com.projectsphere.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentRepository extends JpaRepository<Document, Long> {
    List<Document> findByProject(Project project);
}
