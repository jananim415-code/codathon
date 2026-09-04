package com.projectsphere.service;

import com.projectsphere.entity.User;
import com.projectsphere.exception.ConflictException;
import com.projectsphere.exception.ResourceNotFoundException;
import com.projectsphere.repository.UserRepository;
import com.projectsphere.repository.TaskRepository;
import com.projectsphere.repository.DocumentRepository;
import com.projectsphere.repository.ContributionRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final DocumentRepository documentRepository;
    private final ContributionRepository contributionRepository;

    public UserService(UserRepository userRepository, TaskRepository taskRepository,
                       DocumentRepository documentRepository, ContributionRepository contributionRepository) {
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
        this.documentRepository = documentRepository;
        this.contributionRepository = contributionRepository;
    }

    public User createUser(@Valid User user) {
        user.setEmail(normalizeEmail(user.getEmail()));
        if (userRepository.existsByEmailIgnoreCase(user.getEmail())) {
            throw new ConflictException("An account with this email already exists");
        }
        return userRepository.save(user);
    }

    public List<User> listUsers() {
        return userRepository.findAll();
    }

    public User getUser(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    public User updateUser(Long id, User updatedUser) {
        User existing = getUser(id);
        String email = normalizeEmail(updatedUser.getEmail());
        if (!email.equalsIgnoreCase(existing.getEmail())
            && userRepository.existsByEmailIgnoreCase(email)) {
            throw new ConflictException("An account with this email already exists");
        }
        existing.setName(updatedUser.getName());
        existing.setEmail(email);
        existing.setRole(updatedUser.getRole());
        existing.setGithubUsername(updatedUser.getGithubUsername());
        return userRepository.save(existing);
    }

    @org.springframework.transaction.annotation.Transactional
    public void deleteUser(Long id) {
        User user = getUser(id);
        taskRepository.findByAssignedUser(user).forEach(task -> task.setAssignedUser(null));
        documentRepository.findByCreatedBy(user).forEach(document -> document.setCreatedBy(null));
        contributionRepository.deleteAll(contributionRepository.findByUser(user));
        user.getTeams().forEach(team -> team.getMembers().remove(user));
        user.getTeams().clear();
        userRepository.delete(user);
    }

    private String normalizeEmail(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }
}
