package com.projectsphere.controller;

import com.projectsphere.service.ReportService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/project/{projectId}/user/{userId}")
    public ResponseEntity<byte[]> downloadProjectUserReport(@PathVariable Long projectId, @PathVariable Long userId) {
        byte[] pdf = reportService.generateUserReport(projectId, userId);
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=projectsphere-report-user-" + userId + ".pdf")
            .contentType(MediaType.APPLICATION_PDF)
            .body(pdf);
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<byte[]> downloadProjectReport(@PathVariable Long projectId) {
        byte[] pdf = reportService.generateProjectReport(projectId);
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=projectsphere-report-project-" + projectId + ".pdf")
            .contentType(MediaType.APPLICATION_PDF)
            .body(pdf);
    }
}
