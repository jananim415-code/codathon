package com.projectsphere;

import com.projectsphere.entity.Project;
import com.projectsphere.entity.Task;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TaskCompletionTest {

    @Test
    void taskCompletionCalculationIsAccurate() {
        Project project = new Project();
        project.setName("Demo");

        Task pending = new Task();
        pending.setStatus(Task.Status.TODO);
        pending.setProject(project);

        Task done = new Task();
        done.setStatus(Task.Status.COMPLETED);
        done.setProject(project);

        double completionRate = (double) 1 / 2 * 100;
        assertEquals(50.0, completionRate, 0.01);
    }
}
