package com.projectsphere.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class GitHubConfig {

    @Bean
    public RestClient githubRestClient(@Value("${app.github.token:}") String token) {
        RestClient.Builder builder = RestClient.builder();
        if (token != null && !token.isBlank()) {
            builder.defaultHeader("Authorization", "Bearer " + token);
            builder.defaultHeader("Accept", "application/vnd.github+json");
        }
        return builder.build();
    }
}
