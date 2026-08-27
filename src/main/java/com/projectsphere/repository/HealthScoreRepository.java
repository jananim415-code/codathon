package com.projectsphere.repository;

import com.projectsphere.entity.HealthScore;
import com.projectsphere.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface HealthScoreRepository extends JpaRepository<HealthScore, Long> {
    List<HealthScore> findByProject(Project project);
    Optional<HealthScore> findTopByProjectOrderByCalculatedAtDesc(Project project);
}
