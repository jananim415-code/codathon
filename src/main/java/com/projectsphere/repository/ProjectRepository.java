package com.projectsphere.repository;

import com.projectsphere.entity.Project;
import com.projectsphere.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByTeam(Team team);
}
