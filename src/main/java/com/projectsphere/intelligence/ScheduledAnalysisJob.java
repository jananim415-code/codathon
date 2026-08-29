package com.projectsphere.intelligence;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledAnalysisJob {

    @Scheduled(fixedDelayString = "${app.analysis.fixed-delay-ms:300000}")
    public void runScheduledAnalysis() {
        // Scheduled job for periodic team intelligence analysis
        // Can be extended with external service calls when needed
    }
}
