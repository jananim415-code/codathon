package com.projectsphere;

import com.projectsphere.entity.User;
import com.projectsphere.intelligence.ContributionScorer;
import com.projectsphere.intelligence.FreeRiderDetector;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FreeRiderDetectorTest {

    @Test
    void freeRiderDetectorFlagsLowContributionMember() {
        User high = new User();
        high.setName("High");
        User low = new User();
        low.setName("Low");

        ContributionScorer.ContributionScoreResult strong = new ContributionScorer.ContributionScoreResult(high, 91.0, 25, 20, 15, 18, 10, "high");
        ContributionScorer.ContributionScoreResult weak = new ContributionScorer.ContributionScoreResult(low, 24.0, 5, 5, 4, 1, 2, "low");

        FreeRiderDetector detector = new FreeRiderDetector();
        List<FreeRiderDetector.FreeRiderResult> results = detector.detect(List.of(strong, weak));
        assertTrue(results.stream().anyMatch(r -> r.isFlagged() && r.getUser().getName().equals("Low")));
    }
}
