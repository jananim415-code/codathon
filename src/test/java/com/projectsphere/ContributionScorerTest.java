package com.projectsphere;

import com.projectsphere.entity.*;
import com.projectsphere.intelligence.ContributionScorer;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ContributionScorerTest {

    @Test
    void contributionScorerReturnsScoreWithinRange() {
        User user = new User();
        user.setName("Aisha");

        Project project = new Project();
        project.setName("Demo");

        Contribution contribution = new Contribution();
        contribution.setProject(project);
        contribution.setUser(user);
        contribution.setCommitCount(32);
        contribution.setPullRequestCount(6);
        contribution.setFilesChanged(42);
        contribution.setCodeChurn(850);
        contribution.setTrivialChanges(20);

        Task task = new Task();
        task.setAssignedUser(user);
        task.setStatus(Task.Status.COMPLETED);

        ContributionScorer scorer = new ContributionScorer();
        ContributionScorer.ContributionScoreResult result = scorer.scoreUser(user, List.of(contribution), List.of(task), project);

        assertTrue(result.getScore() >= 0.0);
        assertTrue(result.getScore() <= 100.0);
        assertNotNull(result.getExplanation());
    }
}
