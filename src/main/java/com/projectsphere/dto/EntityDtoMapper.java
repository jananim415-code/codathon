package com.projectsphere.dto;

import com.projectsphere.entity.Document;
import com.projectsphere.entity.Project;
import com.projectsphere.entity.Task;
import com.projectsphere.entity.Team;
import com.projectsphere.entity.User;

import java.util.List;

public final class EntityDtoMapper {

    private EntityDtoMapper() {
    }

    public static User toEntity(UserRequest request) {
        User user = new User();
        user.setName(request.name());
        user.setEmail(request.email());
        if (request.role() != null) {
            user.setRole(request.role());
        }
        user.setGithubUsername(request.githubUsername());
        return user;
    }

    public static UserResponse toResponse(User user) {
        List<ApiReference> teams = user.getTeams().stream()
            .map(team -> reference(team.getId(), team.getName()))
            .toList();
        return new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getRole(),
            user.getGithubUsername(), user.getCreatedAt(), teams);
    }

    public static Team toEntity(TeamRequest request) {
        Team team = new Team();
        team.setName(request.name());
        team.setDescription(request.description());
        return team;
    }

    public static TeamResponse toResponse(Team team) {
        List<ApiReference> members = team.getMembers().stream()
            .map(user -> reference(user.getId(), user.getName()))
            .toList();
        List<ApiReference> projects = team.getProjects().stream()
            .map(project -> reference(project.getId(), project.getName()))
            .toList();
        return new TeamResponse(team.getId(), team.getName(), team.getDescription(),
            team.getCreatedAt(), members, projects);
    }

    public static Project toEntity(ProjectRequest request) {
        Project project = new Project();
        project.setName(request.name());
        project.setDescription(request.description());
        project.setDeadline(request.deadline());
        project.setGithubRepositoryUrl(request.githubRepositoryUrl());
        if (request.status() != null) {
            project.setStatus(request.status());
        }
        if (request.teamId() != null) {
            Team team = new Team();
            team.setId(request.teamId());
            project.setTeam(team);
        }
        return project;
    }

    public static ProjectResponse toResponse(Project project) {
        ApiReference team = project.getTeam() == null
            ? null
            : reference(project.getTeam().getId(), project.getTeam().getName());
        return new ProjectResponse(project.getId(), project.getName(), project.getDescription(),
            project.getDeadline(), project.getGithubRepositoryUrl(), project.getStatus(),
            project.getCreatedAt(), team);
    }

    public static Task toEntity(TaskRequest request) {
        Task task = new Task();
        task.setTitle(request.title());
        task.setDescription(request.description());
        if (request.status() != null) {
            task.setStatus(request.status());
        }
        if (request.priority() != null) {
            task.setPriority(request.priority());
        }
        task.setDueDate(request.dueDate());
        if (request.assignedUserId() != null) {
            User user = new User();
            user.setId(request.assignedUserId());
            task.setAssignedUser(user);
        }
        if (request.projectId() != null) {
            Project project = new Project();
            project.setId(request.projectId());
            task.setProject(project);
        }
        return task;
    }

    public static TaskResponse toResponse(Task task) {
        ApiReference assignedUser = task.getAssignedUser() == null
            ? null
            : reference(task.getAssignedUser().getId(), task.getAssignedUser().getName());
        ApiReference project = task.getProject() == null
            ? null
            : reference(task.getProject().getId(), task.getProject().getName());
        return new TaskResponse(task.getId(), task.getTitle(), task.getDescription(), task.getStatus(),
            task.getPriority(), task.getDueDate(), task.getCompletedAt(), task.getCreatedAt(),
            assignedUser, project);
    }

    public static Document toEntity(DocumentRequest request) {
        Document document = new Document();
        document.setTitle(request.title());
        document.setContent(request.content());
        if (request.projectId() != null) {
            Project project = new Project();
            project.setId(request.projectId());
            document.setProject(project);
        }
        if (request.createdById() != null) {
            User user = new User();
            user.setId(request.createdById());
            document.setCreatedBy(user);
        }
        return document;
    }

    public static DocumentResponse toResponse(Document document) {
        ApiReference project = document.getProject() == null
            ? null
            : reference(document.getProject().getId(), document.getProject().getName());
        ApiReference createdBy = document.getCreatedBy() == null
            ? null
            : reference(document.getCreatedBy().getId(), document.getCreatedBy().getName());
        return new DocumentResponse(document.getId(), document.getTitle(), document.getContent(),
            document.getCreatedAt(), document.getUpdatedAt(), project, createdBy);
    }

    private static ApiReference reference(Long id, String name) {
        return new ApiReference(id, name);
    }
}
