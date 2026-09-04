package com.projectsphere.github;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.net.URI;
import java.net.URISyntaxException;

@Component
public class GitHubClient {
    private static final Logger log = LoggerFactory.getLogger(GitHubClient.class);

    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final String demoRepository;
    private final String token;
    private final boolean demoMode;

    public GitHubClient(RestClient restClient,
                       ObjectMapper objectMapper,
                       @Value("${app.github.demo-repository:https://github.com/example/student-project}") String demoRepository,
                       @Value("${app.github.token:}") String token,
                       @Value("${app.demo-mode:true}") boolean demoMode) {
        this.restClient = restClient;
        this.objectMapper = objectMapper;
        this.demoRepository = demoRepository;
        this.token = token;
        this.demoMode = demoMode;
    }

    public String getRepository() {
        return demoRepository;
    }

    public List<GitHubCommit> getCommits(String repositoryUrl) {
        if (token == null || token.isBlank()) {
            return fallbackCommits();
        }

        String[] parts = extractOwnerAndRepo(repositoryUrl);
        if (parts == null) {
            return fallbackCommits();
        }

        try {
            ResponseEntity<String> response = restClient.get()
                .uri("https://api.github.com/repos/{owner}/{repo}/commits", parts[0], parts[1])
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .toEntity(String.class);
            if (!response.getStatusCode().is2xxSuccessful()) {
                return fallbackCommits();
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
            return commits.isEmpty() ? fallbackCommits() : commits;
        } catch (Exception ex) {
            log.warn("GitHub commits request failed; using fallback data: {}", ex.getClass().getSimpleName());
            return fallbackCommits();
        }
    }

    public List<GitHubPullRequest> getPullRequests(String repositoryUrl) {
        if (token == null || token.isBlank()) {
            return fallbackPullRequests();
        }

        String[] parts = extractOwnerAndRepo(repositoryUrl);
        if (parts == null) {
            return fallbackPullRequests();
        }

        try {
            ResponseEntity<String> response = restClient.get()
                .uri("https://api.github.com/repos/{owner}/{repo}/pulls", parts[0], parts[1])
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .toEntity(String.class);
            if (!response.getStatusCode().is2xxSuccessful()) {
                return fallbackPullRequests();
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
            return pulls.isEmpty() ? fallbackPullRequests() : pulls;
        } catch (Exception ex) {
            log.warn("GitHub pull requests request failed; using fallback data: {}", ex.getClass().getSimpleName());
            return fallbackPullRequests();
        }
    }

    public List<String> getContributorActivity(String repositoryUrl) {
        if (token == null || token.isBlank()) {
            return fallbackActivity();
        }

        String[] parts = extractOwnerAndRepo(repositoryUrl);
        if (parts == null) {
            return fallbackActivity();
        }

        try {
            ResponseEntity<String> response = restClient.get()
                .uri("https://api.github.com/repos/{owner}/{repo}/events", parts[0], parts[1])
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .toEntity(String.class);
            if (!response.getStatusCode().is2xxSuccessful()) {
                return fallbackActivity();
            }
            return parseActivity(response.getBody());
        } catch (Exception ex) {
            log.warn("GitHub activity request failed; using fallback data: {}", ex.getClass().getSimpleName());
            return fallbackActivity();
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
        return activities.isEmpty() ? fallbackActivity() : activities;
    }

    private List<GitHubCommit> fallbackCommits() {
        return demoMode ? demoCommits() : List.of();
    }

    private List<GitHubPullRequest> fallbackPullRequests() {
        return demoMode ? demoPullRequests() : List.of();
    }

    private List<String> fallbackActivity() {
        return demoMode ? List.of(
            "Aisha pushed updates to Smart Campus Assistant",
            "Rahul reviewed documentation updates",
            "Priya merged a feature branch",
            "Demo GitHub Data"
        ) : List.of();
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
        try {
            URI uri = new URI(repositoryUrl.trim());
            if (!"https".equalsIgnoreCase(uri.getScheme()) ||
                !"github.com".equalsIgnoreCase(uri.getHost()) ||
                uri.getUserInfo() != null) {
                return null;
            }
            String path = uri.getPath();
            if (path == null) return null;
            String[] repoParts = path.replaceFirst("^/", "").replaceFirst("/$", "").split("/");
            if (repoParts.length != 2 || repoParts[0].isBlank() || repoParts[1].isBlank()) return null;
            String repo = repoParts[1].endsWith(".git")
                ? repoParts[1].substring(0, repoParts[1].length() - 4) : repoParts[1];
            return repo.isBlank() ? null : new String[] { repoParts[0], repo };
        } catch (URISyntaxException ex) {
            return null;
        }
    }
}
