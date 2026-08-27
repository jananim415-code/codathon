package com.projectsphere;

import com.projectsphere.entity.Project;
import com.projectsphere.intelligence.ProjectHealthCalculator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProjectHealthCalculatorTest {

    @Test
    void healthScoreClassifiesCorrectly() {
        Project project = new Project();
        project.setName("Demo");

        ProjectHealthCalculator calculator = new ProjectHealthCalculator();
        ProjectHealthCalculator.HealthResult result = calculator.calculate(project, java.util.List.of(), 80.0, 75.0);

        assertTrue(result.getHealthScore() >= 0.0);
        assertTrue(result.getHealthScore() <= 100.0);
        assertNotNull(result.getStatus());
    }
}
