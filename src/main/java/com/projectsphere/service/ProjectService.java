package com.projectsphere.service;

import com.projectsphere.entity.Project;
import com.projectsphere.entity.Team;
import com.projectsphere.exception.ResourceNotFoundException;
import com.projectsphere.repository.ProjectRepository;
import com.projectsphere.repository.TeamRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final TeamRepository teamRepository;

    public ProjectService(ProjectRepository projectRepository, TeamRepository teamRepository) {
        this.projectRepository = projectRepository;
        this.teamRepository = teamRepository;
    }

    public Project createProject(@Valid Project project) {
        if (project.getTeam() != null && project.getTeam().getId() != null) {
            Team team = teamRepository.findById(project.getTeam().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Team not found"));
            project.setTeam(team);
        }
        return projectRepository.save(project);
    }

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
}
