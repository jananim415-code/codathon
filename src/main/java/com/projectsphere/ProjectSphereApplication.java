package com.projectsphere;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ProjectSphereApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProjectSphereApplication.class, args);
    }
}
