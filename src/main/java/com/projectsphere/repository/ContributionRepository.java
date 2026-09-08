package com.projectsphere.repository;

import com.projectsphere.entity.Contribution;
import com.projectsphere.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContributionRepository extends JpaRepository<Contribution, Long> {
    List<Contribution> findByProject(Project project);
}
