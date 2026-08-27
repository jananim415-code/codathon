package com.projectsphere.github;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class GitHubClient {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final String demoRepository;
    private final String token;

    public GitHubClient(RestClient restClient,
                       ObjectMapper objectMapper,
                       @Value("${app.github.demo-repository:https://github.com/example/student-project}") String demoRepository,
                       @Value("${app.github.token:}") String token) {
        this.restClient = restClient;
        this.objectMapper = objectMapper;
        this.demoRepository = demoRepository;
        this.token = token;
    }

    public String getRepository() {
        return demoRepository;
    }

    public List<GitHubCommit> getCommits(String repositoryUrl) {
        if (token == null || token.isBlank()) {
            return demoCommits();
        }

        String[] parts = extractOwnerAndRepo(repositoryUrl);
        if (parts == null) {
            return demoCommits();
        }

        try {
            ResponseEntity<String> response = restClient.get()
                .uri("https://api.github.com/repos/{owner}/{repo}/commits", parts[0], parts[1])
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .toEntity(String.class);
            if (!response.getStatusCode().is2xxSuccessful()) {
                return demoCommits();
            }

            JsonNode node = objectMapper.readTree(response.getBody());
            List<GitHubCommit> commits = new ArrayList<>();
            if (node.isArray()) {
                for (JsonNode item : node) {
                    String sha = item.path("sha").asText();
                    String message = item.path("commit").path("message").asText();
                    String author = item.path("commit").path("author").path("name").asText();
                    String date = item.path("commit").path("author").path("date").asText();
                    commits.add(new GitHubCommit(sha, message, author, date));
                }
            }
            return commits.isEmpty() ? demoCommits() : commits;
        } catch (Exception ex) {
            return demoCommits();
        }
    }

    public List<GitHubPullRequest> getPullRequests(String repositoryUrl) {
        if (token == null || token.isBlank()) {
            return demoPullRequests();
        }

        String[] parts = extractOwnerAndRepo(repositoryUrl);
        if (parts == null) {
            return demoPullRequests();
        }

        try {
            ResponseEntity<String> response = restClient.get()
                .uri("https://api.github.com/repos/{owner}/{repo}/pulls", parts[0], parts[1])
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .toEntity(String.class);
            if (!response.getStatusCode().is2xxSuccessful()) {
                return demoPullRequests();
            }

            JsonNode node = objectMapper.readTree(response.getBody());
            List<GitHubPullRequest> pulls = new ArrayList<>();
            if (node.isArray()) {
                for (JsonNode item : node) {
                    pulls.add(new GitHubPullRequest(
                        item.path("id").asLong(),
                        item.path("title").asText(),
                        item.path("state").asText(),
                        item.path("user").path("login").asText()
                    ));
                }
            }
            return pulls.isEmpty() ? demoPullRequests() : pulls;
        } catch (Exception ex) {
            return demoPullRequests();
        }
    }

    public List<String> getContributorActivity(String repositoryUrl) {
        if (token == null || token.isBlank()) {
            return List.of(
                "Aisha pushed updates to Smart Campus Assistant",
                "Rahul reviewed documentation updates",
                "Priya merged a feature branch",
                "Demo GitHub Data"
            );
        }

        String[] parts = extractOwnerAndRepo(repositoryUrl);
        if (parts == null) {
            return List.of("Demo GitHub Data");
        }

        try {
            ResponseEntity<String> response = restClient.get()
                .uri("https://api.github.com/repos/{owner}/{repo}/events", parts[0], parts[1])
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .toEntity(String.class);
            if (!response.getStatusCode().is2xxSuccessful()) {
                return List.of("Demo GitHub Data");
            }
            return parseActivity(response.getBody());
        } catch (Exception ex) {
            return List.of("Demo GitHub Data");
        }
    }

    private List<String> parseActivity(String json) {
        List<String> activities = new ArrayList<>();
        try {
            JsonNode node = objectMapper.readTree(json);
            if (node.isArray()) {
                for (JsonNode item : node) {
                    String type = item.path("type").asText();
                    String actor = item.path("actor").path("login").asText();
                    if (!type.isBlank()) {
                        activities.add(actor + " triggered " + type);
                    }
                    if (activities.size() >= 5) {
                        break;
                    }
                }
            }
        } catch (IOException ignored) {
        }
        return activities.isEmpty() ? List.of("Demo GitHub Data") : activities;
    }

    private List<GitHubCommit> demoCommits() {
        List<GitHubCommit> commits = new ArrayList<>();
        commits.add(new GitHubCommit("1", "Implement dashboard UI", "Aisha", "2025-01-10T08:00:00Z"));
        commits.add(new GitHubCommit("2", "Add API endpoints", "Rahul", "2025-01-11T10:30:00Z"));
        commits.add(new GitHubCommit("3", "Fix task logic", "Aisha", "2025-01-12T07:25:00Z"));
        commits.add(new GitHubCommit("4", "Add health scoring", "Priya", "2025-01-14T14:40:00Z"));
        return commits;
    }

    private List<GitHubPullRequest> demoPullRequests() {
        List<GitHubPullRequest> pulls = new ArrayList<>();
        pulls.add(new GitHubPullRequest(101L, "Add dashboard improvements", "closed", "Aisha"));
        pulls.add(new GitHubPullRequest(102L, "Update workflow docs", "open", "Rahul"));
        return pulls;
    }

    private String[] extractOwnerAndRepo(String repositoryUrl) {
        if (repositoryUrl == null || repositoryUrl.isBlank()) {
            return null;
        }
        String cleaned = repositoryUrl.trim();
        if (cleaned.endsWith("/")) {
            cleaned = cleaned.substring(0, cleaned.length() - 1);
        }
        String[] parts = cleaned.split("github.com/");
        if (parts.length != 2) {
            return null;
        }
        String[] repoParts = parts[1].split("/");
        if (repoParts.length < 2) {
            return null;
        }
        return new String[] { repoParts[0], repoParts[1] };
    }
}
