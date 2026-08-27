package com.projectsphere.github;

public class GitHubCommit {
    private String sha;
    private String message;
    private String author;
    private String date;

    public GitHubCommit() {}

    public GitHubCommit(String sha, String message, String author, String date) {
        this.sha = sha;
        this.message = message;
        this.author = author;
        this.date = date;
    }

    public String getSha() { return sha; }
    public void setSha(String sha) { this.sha = sha; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
}
