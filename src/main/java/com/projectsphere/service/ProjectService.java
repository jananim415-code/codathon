package com.projectsphere.service;

import com.projectsphere.entity.Project;
import com.projectsphere.entity.Team;
import com.projectsphere.exception.ResourceNotFoundException;
import com.projectsphere.repository.ProjectRepository;
import com.projectsphere.repository.TeamRepository;
import com.projectsphere.repository.TaskRepository;
import com.projectsphere.repository.DocumentRepository;
import com.projectsphere.repository.ContributionRepository;
import com.projectsphere.repository.HealthScoreRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final TeamRepository teamRepository;
    private final TaskRepository taskRepository;
    private final DocumentRepository documentRepository;
    private final ContributionRepository contributionRepository;
    private final HealthScoreRepository healthScoreRepository;

    public ProjectService(ProjectRepository projectRepository, TeamRepository teamRepository,
                          TaskRepository taskRepository, DocumentRepository documentRepository,
                          ContributionRepository contributionRepository, HealthScoreRepository healthScoreRepository) {
        this.projectRepository = projectRepository;
        this.teamRepository = teamRepository;
        this.taskRepository = taskRepository;
        this.documentRepository = documentRepository;
        this.contributionRepository = contributionRepository;
        this.healthScoreRepository = healthScoreRepository;
    }

    public Project createProject(@Valid Project project) {
        if (project.getTeam() != null && project.getTeam().getId() != null) {
            Team team = teamRepository.findById(project.getTeam().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Team not found"));
            project.setTeam(team);
        }
        return projectRepository.save(project);
    }

    @Transactional(readOnly = true)
    public List<Project> listProjects() {
        return projectRepository.findAll();
    }

    public Project getProject(Long id) {
        return projectRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Project not found"));
    }

    public Project updateProject(Long id, Project updatedProject) {
        Project existing = getProject(id);
        existing.setName(updatedProject.getName());
        existing.setDescription(updatedProject.getDescription());
        existing.setDeadline(updatedProject.getDeadline());
        existing.setGithubRepositoryUrl(updatedProject.getGithubRepositoryUrl());
        existing.setStatus(updatedProject.getStatus());
        if (updatedProject.getTeam() != null && updatedProject.getTeam().getId() != null) {
            Team team = teamRepository.findById(updatedProject.getTeam().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Team not found"));
            existing.setTeam(team);
        }
        return projectRepository.save(existing);
    }

    @Transactional
    public void deleteProject(Long id) {
        Project project = getProject(id);
        // Explicitly remove dependent rows because the schema intentionally uses
        // restrictive foreign keys and projects own their task/document history.
        taskRepository.deleteAll(taskRepository.findByProject(project));
        documentRepository.deleteAll(documentRepository.findByProject(project));
        contributionRepository.deleteAll(contributionRepository.findByProject(project));
        healthScoreRepository.deleteAll(healthScoreRepository.findByProject(project));
        projectRepository.delete(project);
    }
}
