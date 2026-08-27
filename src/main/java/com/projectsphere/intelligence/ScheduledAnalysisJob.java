package com.projectsphere.intelligence;

import com.projectsphere.service.IntelligenceService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledAnalysisJob {

    private final IntelligenceService intelligenceService;

    public ScheduledAnalysisJob(IntelligenceService intelligenceService) {
        this.intelligenceService = intelligenceService;
    }

    @Scheduled(fixedDelayString = "${app.analysis.fixed-delay-ms:300000}")
    public void runScheduledAnalysis() {
        intelligenceService.analyzeAllProjects();
    }
}
