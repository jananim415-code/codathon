package com.projectsphere.service;

import com.projectsphere.entity.Team;
import com.projectsphere.entity.User;
import com.projectsphere.exception.ResourceNotFoundException;
import com.projectsphere.repository.TeamRepository;
import com.projectsphere.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeamService {

    private final TeamRepository teamRepository;
    private final UserRepository userRepository;

    public TeamService(TeamRepository teamRepository, UserRepository userRepository) {
        this.teamRepository = teamRepository;
        this.userRepository = userRepository;
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
}
