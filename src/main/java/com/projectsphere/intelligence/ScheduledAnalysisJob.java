package com.projectsphere.intelligence;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.projectsphere.service.IntelligenceService;

@Component
public class ScheduledAnalysisJob {
    private static final Logger log = LoggerFactory.getLogger(ScheduledAnalysisJob.class);
    private final IntelligenceService intelligenceService;

    public ScheduledAnalysisJob(IntelligenceService intelligenceService) {
        this.intelligenceService = intelligenceService;
    }

    @Scheduled(fixedDelayString = "${app.analysis.fixed-delay-ms:300000}")
    public void runScheduledAnalysis() {
        try {
            intelligenceService.analyzeAllProjects();
            log.debug("Scheduled project intelligence analysis completed");
        } catch (RuntimeException ex) {
            log.error("Scheduled project intelligence analysis failed", ex);
        }
    }
}
