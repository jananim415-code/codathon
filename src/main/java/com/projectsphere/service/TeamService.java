package com.projectsphere.service;

import com.projectsphere.entity.Team;
import com.projectsphere.entity.User;
import com.projectsphere.exception.ResourceNotFoundException;
import com.projectsphere.repository.TeamRepository;
import com.projectsphere.repository.UserRepository;
import com.projectsphere.repository.ProjectRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeamService {

    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;

    public TeamService(TeamRepository teamRepository, UserRepository userRepository,
                       ProjectRepository projectRepository) {
        this.teamRepository = teamRepository;
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
    }

    public Team createTeam(@Valid Team team) {
        return teamRepository.save(team);
    }

    public List<Team> listTeams() {
        return teamRepository.findAll();
    }

    public Team getTeam(Long id) {
        return teamRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Team not found"));
    }

    public Team addMember(Long teamId, Long userId) {
        Team team = getTeam(teamId);
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (!team.getMembers().contains(user)) {
            team.getMembers().add(user);
            user.getTeams().add(team);
        }
        return teamRepository.save(team);
    }

    public void removeMember(Long teamId, Long userId) {
        Team team = getTeam(teamId);
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        team.getMembers().remove(user);
        user.getTeams().remove(team);
        teamRepository.save(team);
        userRepository.save(user);
    }

    public Team updateTeam(Long id, Team updated) {
        Team existing = getTeam(id);
        existing.setName(updated.getName());
        existing.setDescription(updated.getDescription());
        return teamRepository.save(existing);
    }

    @org.springframework.transaction.annotation.Transactional
    public void deleteTeam(Long id) {
        Team team = getTeam(id);
        projectRepository.findByTeam(team).forEach(project -> project.setTeam(null));
        team.getMembers().forEach(user -> user.getTeams().remove(team));
        team.getMembers().clear();
        teamRepository.delete(team);
    }
}
