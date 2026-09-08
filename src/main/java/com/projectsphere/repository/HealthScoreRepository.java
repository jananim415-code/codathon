package com.projectsphere.repository;

import com.projectsphere.entity.HealthScore;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HealthScoreRepository extends JpaRepository<HealthScore, Long> {
}
