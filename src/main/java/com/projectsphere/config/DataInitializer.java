package com.projectsphere.config;

import com.projectsphere.entity.*;
import com.projectsphere.intelligence.ProjectHealthCalculator;
import com.projectsphere.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.time.LocalDate;
import java.util.List;

@Configuration
@Profile("dev")
public class DataInitializer {

    @Bean
    CommandLineRunner initData(UserRepository userRepository,
                               TeamRepository teamRepository,
                               ProjectRepository projectRepository,
                               TaskRepository taskRepository,
                               DocumentRepository documentRepository,
                               ContributionRepository contributionRepository,
                               HealthScoreRepository healthScoreRepository) {
        return args -> {
            if (userRepository.count() > 0) {
                return;
            }

            User aisha = new User();
            aisha.setName("Aisha");
            aisha.setEmail("aisha@projectsphere.com");
            aisha.setRole(User.Role.TEAM_LEAD);
            aisha.setGithubUsername("aisha-dev");
            userRepository.save(aisha);

            User rahul = new User();
            rahul.setName("Rahul");
            rahul.setEmail("rahul@projectsphere.com");
            rahul.setRole(User.Role.STUDENT);
            rahul.setGithubUsername("rahul-code");
            userRepository.save(rahul);

            User priya = new User();
            priya.setName("Priya");
            priya.setEmail("priya@projectsphere.com");
            priya.setRole(User.Role.STUDENT);
            priya.setGithubUsername("priya-ai");
            userRepository.save(priya);

            User arjun = new User();
            arjun.setName("Arjun");
            arjun.setEmail("arjun@projectsphere.com");
            arjun.setRole(User.Role.STUDENT);
            arjun.setGithubUsername("arjun-ux");
            userRepository.save(arjun);

            Team team = new Team();
            team.setName("Team Phoenix");
            team.setDescription("Student engineering team working on campus technology solutions.");
            teamRepository.save(team);

            team.setMembers(List.of(aisha, rahul, priya, arjun));
            aisha.setTeams(List.of(team));
            rahul.setTeams(List.of(team));
            priya.setTeams(List.of(team));
            arjun.setTeams(List.of(team));
            userRepository.saveAll(List.of(aisha, rahul, priya, arjun));
            teamRepository.save(team);

            Project project = new Project();
            project.setName("Smart Campus Assistant");
            project.setDescription("AI-powered platform for improving student campus services.");
            project.setDeadline(LocalDate.now().plusDays(30));
            project.setGithubRepositoryUrl("https://github.com/example/student-project");
            project.setStatus(Project.Status.ACTIVE);
            project.setTeam(team);
            projectRepository.save(project);

            Task task1 = new Task(); task1.setTitle("Design database"); task1.setDescription("Schema and ERD"); task1.setStatus(Task.Status.COMPLETED); task1.setPriority(Task.Priority.HIGH); task1.setDueDate(LocalDate.now().minusDays(5)); task1.setAssignedUser(aisha); task1.setProject(project); taskRepository.save(task1);
            Task task2 = new Task(); task2.setTitle("Build authentication API"); task2.setDescription("Login and JWT flow"); task2.setStatus(Task.Status.IN_PROGRESS); task2.setPriority(Task.Priority.HIGH); task2.setDueDate(LocalDate.now().plusDays(8)); task2.setAssignedUser(rahul); task2.setProject(project); taskRepository.save(task2);
            Task task3 = new Task(); task3.setTitle("Create dashboard UI"); task3.setDescription("Responsive user dashboard"); task3.setStatus(Task.Status.COMPLETED); task3.setPriority(Task.Priority.MEDIUM); task3.setDueDate(LocalDate.now().minusDays(2)); task3.setAssignedUser(priya); task3.setProject(project); taskRepository.save(task3);
            Task task4 = new Task(); task4.setTitle("Implement task management"); task4.setDescription("Create tasks and statuses"); task4.setStatus(Task.Status.IN_PROGRESS); task4.setPriority(Task.Priority.HIGH); task4.setDueDate(LocalDate.now().plusDays(10)); task4.setAssignedUser(rahul); task4.setProject(project); taskRepository.save(task4);
            Task task5 = new Task(); task5.setTitle("Integrate GitHub API"); task5.setDescription("Sync repository data"); task5.setStatus(Task.Status.COMPLETED); task5.setPriority(Task.Priority.HIGH); task5.setDueDate(LocalDate.now().plusDays(3)); task5.setAssignedUser(aisha); task5.setProject(project); taskRepository.save(task5);
            Task task6 = new Task(); task6.setTitle("Implement contribution scorer"); task6.setDescription("Weighted contribution metrics"); task6.setStatus(Task.Status.COMPLETED); task6.setPriority(Task.Priority.MEDIUM); task6.setDueDate(LocalDate.now().plusDays(4)); task6.setAssignedUser(priya); task6.setProject(project); taskRepository.save(task6);
            Task task7 = new Task(); task7.setTitle("Implement health score"); task7.setDescription("Project health algorithm"); task7.setStatus(Task.Status.TODO); task7.setPriority(Task.Priority.MEDIUM); task7.setDueDate(LocalDate.now().plusDays(15)); task7.setAssignedUser(rahul); task7.setProject(project); taskRepository.save(task7);
            Task task8 = new Task(); task8.setTitle("Prepare final documentation"); task8.setDescription("Project overview and setup"); task8.setStatus(Task.Status.TODO); task8.setPriority(Task.Priority.LOW); task8.setDueDate(LocalDate.now().plusDays(20)); task8.setAssignedUser(arjun); task8.setProject(project); taskRepository.save(task8);

            Document doc1 = new Document(); doc1.setTitle("Project Charter"); doc1.setContent("Project objectives and milestones."); doc1.setProject(project); doc1.setCreatedBy(aisha); documentRepository.save(doc1);
            Document doc2 = new Document(); doc2.setTitle("Architecture Notes"); doc2.setContent("Service and data layer structure overview."); doc2.setProject(project); doc2.setCreatedBy(priya); documentRepository.save(doc2);
            Document doc3 = new Document(); doc3.setTitle("Team Roles"); doc3.setContent("Responsibilities by member and sprint plan."); doc3.setProject(project); doc3.setCreatedBy(rahul); documentRepository.save(doc3);

            Contribution c1 = new Contribution(); c1.setProject(project); c1.setUser(aisha); c1.setCommitCount(32); c1.setPullRequestCount(6); c1.setFilesChanged(42); c1.setCodeChurn(850); c1.setTrivialChanges(20); c1.setAnalysisDate(java.time.LocalDateTime.now()); contributionRepository.save(c1);
            Contribution c2 = new Contribution(); c2.setProject(project); c2.setUser(rahul); c2.setCommitCount(25); c2.setPullRequestCount(4); c2.setFilesChanged(31); c2.setCodeChurn(600); c2.setTrivialChanges(30); c2.setAnalysisDate(java.time.LocalDateTime.now()); contributionRepository.save(c2);
            Contribution c3 = new Contribution(); c3.setProject(project); c3.setUser(priya); c3.setCommitCount(18); c3.setPullRequestCount(3); c3.setFilesChanged(24); c3.setCodeChurn(400); c3.setTrivialChanges(25); c3.setAnalysisDate(java.time.LocalDateTime.now()); contributionRepository.save(c3);
            Contribution c4 = new Contribution(); c4.setProject(project); c4.setUser(arjun); c4.setCommitCount(3); c4.setPullRequestCount(0); c4.setFilesChanged(4); c4.setCodeChurn(30); c4.setTrivialChanges(12); c4.setAnalysisDate(java.time.LocalDateTime.now()); contributionRepository.save(c4);

            HealthScore healthScore = new HealthScore();
            healthScore.setProject(project);
            double taskCompletionRate = 4 * 100.0 / 8;
            ProjectHealthCalculator.HealthResult health = new ProjectHealthCalculator()
                .calculate(project, List.of(task1, task2, task3, task4, task5, task6, task7, task8), 80.0, taskCompletionRate);
            healthScore.setCommitTrend(health.getCommitTrend());
            healthScore.setTaskCompletionRate(health.getTaskCompletionRate());
            healthScore.setDeadlineScore(health.getDeadlineScore());
            healthScore.setHealthScore(health.getHealthScore());
            healthScore.setStatus(HealthScore.Status.valueOf(health.getStatus()));
            healthScore.setCalculatedAt(java.time.LocalDateTime.now());
            healthScoreRepository.save(healthScore);
        };
    }
}
