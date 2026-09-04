package com.projectsphere.controller;

import com.projectsphere.dto.EntityDtoMapper;
import com.projectsphere.dto.TeamRequest;
import com.projectsphere.dto.TeamResponse;
import com.projectsphere.service.TeamService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class TeamController {

    private final TeamService teamService;

    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    @PostMapping("/teams")
    public ResponseEntity<TeamResponse> createTeam(@Valid @RequestBody TeamRequest request) {
        return ResponseEntity.ok(EntityDtoMapper.toResponse(teamService.createTeam(EntityDtoMapper.toEntity(request))));
    }

    @GetMapping("/teams")
    public ResponseEntity<List<TeamResponse>> listTeams() {
        return ResponseEntity.ok(teamService.listTeams().stream().map(EntityDtoMapper::toResponse).toList());
    }

    @GetMapping("/teams/{id}")
    public ResponseEntity<TeamResponse> getTeam(@PathVariable Long id) {
        return ResponseEntity.ok(EntityDtoMapper.toResponse(teamService.getTeam(id)));
    }

    @PutMapping("/teams/{id}")
    public ResponseEntity<TeamResponse> updateTeam(@PathVariable Long id, @Valid @RequestBody TeamRequest request) {
        return ResponseEntity.ok(EntityDtoMapper.toResponse(teamService.updateTeam(id, EntityDtoMapper.toEntity(request))));
    }

    @DeleteMapping("/teams/{id}")
    public ResponseEntity<Void> deleteTeam(@PathVariable Long id) {
        teamService.deleteTeam(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/teams/{teamId}/members/{userId}")
    public ResponseEntity<TeamResponse> addMember(@PathVariable Long teamId, @PathVariable Long userId) {
        return ResponseEntity.ok(EntityDtoMapper.toResponse(teamService.addMember(teamId, userId)));
    }

    @DeleteMapping("/teams/{teamId}/members/{userId}")
    public ResponseEntity<Void> removeMember(@PathVariable Long teamId, @PathVariable Long userId) {
        teamService.removeMember(teamId, userId);
        return ResponseEntity.noContent().build();
    }
}
