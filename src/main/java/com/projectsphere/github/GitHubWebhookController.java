package com.projectsphere.github;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/github")
public class GitHubWebhookController {

    @PostMapping("/webhook")
    public ResponseEntity<Map<String, Object>> receiveWebhook(@RequestBody Map<String, Object> payload) {
        String event = (String) payload.get("zen");
        String action = payload.containsKey("action") ? payload.get("action").toString() : "received";
        Map<String, Object> response = new HashMap<>();
        response.put("status", "accepted");
        response.put("message", "GitHub webhook received successfully.");
        response.put("eventType", event != null ? "zen" : action);
        response.put("payload", payload);
        return ResponseEntity.ok(response);
    }
}
