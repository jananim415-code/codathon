package com.projectsphere.github;

public class GitHubPullRequest {
    private Long id;
    private String title;
    private String state;
    private String user;

    public GitHubPullRequest() {}

    public GitHubPullRequest(Long id, String title, String state, String user) {
        this.id = id;
        this.title = title;
        this.state = state;
        this.user = user;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
    public String getUser() { return user; }
    public void setUser(String user) { this.user = user; }
}
