package com.projectsphere.repository;

import com.projectsphere.entity.Contribution;
import com.projectsphere.entity.Project;
import com.projectsphere.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ContributionRepository extends JpaRepository<Contribution, Long> {
    List<Contribution> findByProject(Project project);
    List<Contribution> findByUser(User user);
    Optional<Contribution> findByProjectAndUser(Project project, User user);
}
