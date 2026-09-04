package com.projectsphere.repository;

import com.projectsphere.entity.Project;
import com.projectsphere.entity.Task;
import com.projectsphere.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByProject(Project project);
    List<Task> findByAssignedUser(User user);
}
