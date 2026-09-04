package com.projectsphere.github;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Value;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/github")
public class GitHubWebhookController {
    private final String webhookSecret;

    public GitHubWebhookController(@Value("${app.github.webhook-secret:}") String webhookSecret) {
        this.webhookSecret = webhookSecret;
    }

    @PostMapping("/webhook")
    public ResponseEntity<Map<String, Object>> receiveWebhook(
        @RequestHeader(value = "X-Hub-Signature-256", required = false) String signature,
        @RequestHeader(value = "X-GitHub-Event", required = false) String githubEvent,
        @RequestBody String rawPayload) {
        if (!webhookSecret.isBlank() && !isValidSignature(signature, rawPayload)) {
            return ResponseEntity.status(401).body(Map.of("status", "rejected", "message", "Invalid webhook signature"));
        }
        Map<String, Object> payload;
        try {
            payload = new com.fasterxml.jackson.databind.ObjectMapper().readValue(rawPayload, Map.class);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(Map.of("status", "rejected", "message", "Malformed webhook payload"));
        }
        String event = (String) payload.get("zen");
        String action = payload.containsKey("action") ? payload.get("action").toString() : "received";
        Map<String, Object> response = new HashMap<>();
        response.put("status", "accepted");
        response.put("message", "GitHub webhook received successfully.");
        response.put("eventType", githubEvent != null ? githubEvent : (event != null ? "zen" : action));
        return ResponseEntity.ok(response);
    }

    private boolean isValidSignature(String signature, String payload) {
        if (signature == null || !signature.startsWith("sha256=")) return false;
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(webhookSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            String expected = "sha256=" + java.util.HexFormat.of().formatHex(mac.doFinal(payload.getBytes(StandardCharsets.UTF_8)));
            return MessageDigest.isEqual(expected.getBytes(StandardCharsets.US_ASCII),
                signature.getBytes(StandardCharsets.US_ASCII));
        } catch (Exception ex) {
            return false;
        }
    }
}
