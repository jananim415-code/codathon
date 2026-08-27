# ProjectSphere

## Unified Student Collaboration & Team Intelligence Platform

### Overview
Student teams often use multiple disconnected platforms for source code, tasks, documentation, communication, and progress tracking. ProjectSphere centralizes these workflows and adds a Team Intelligence Engine for objective contribution analysis and project-health monitoring.

### Key Features
- Team management
- User management
- Project management
- Task management
- Documentation management
- GitHub integration
- Commit tracking
- Pull request tracking
- Contribution scoring
- Free-rider detection
- Project health scoring
- Scheduled analysis
- PDF contribution reports
- Dashboard
- Rule-based recommendations

### Architecture

```mermaid
flowchart TD
    A[Presentation / API Layer] --> B[Service Layer]
    B --> C[Team Intelligence Engine]
    C --> D[GitHub Integration]
    D --> E[Report Generation]
    E --> F[Data Access Layer]
    F --> G[PostgreSQL]
```

The layers work as follows:
- Presentation/API Layer: exposes REST endpoints and the static frontend.
- Service Layer: coordinates business logic across users, teams, projects, tasks, documents, GitHub, intelligence, and reports.
- Team Intelligence Engine: calculates contribution scores, detects low-contributors, and evaluates project health using transparent formulas.
- GitHub Integration: fetches repository metadata and optionally uses a real GitHub token when provided.
- Report Generation: creates PDF reports for individual students and team-level summaries.
- Data Access Layer: persists application state using Spring Data JPA.
- PostgreSQL: production-ready relational database; H2 is used for demo mode.

### Technology Stack
- Java 21
- Spring Boot
- Spring MVC
- Spring Data JPA
- Hibernate
- PostgreSQL
- H2
- GitHub REST API
- Apache PDFBox
- Maven
- HTML/CSS/JavaScript

### Intelligence Engine
ProjectSphere uses three explainable components:

1. Contribution Scorer
2. Free-Rider Detector
3. Project Health Score

#### Contribution Formula
Contribution Score =
Commit Score
+ Code Churn Score
+ Files Score
+ Pull Request Score
+ Task Score

Default weights:
- Commit Score = normalized commits × 30
- Code Churn Score = normalized meaningful churn × 25
- Files Score = normalized files touched × 15
- Pull Request Score = normalized pull requests × 20
- Task Score = task completion rate × 10

Trivial changes reduce effective churn using:
effectiveChurn = totalChurn - trivialChanges

#### Free-Rider Detection
For each member:

z = (memberScore - teamMean) / standardDeviation

Default threshold:
- z <= -1.0

When the threshold is reached, the system labels the member as a "Potential low-contribution member" rather than automatically accusing them.

#### Project Health Formula
Health Score =
(Commit Trend × 0.40)
+ (Task Completion × 0.40)
+ (Deadline Score × 0.20)

Health Classification:
- 80–100 → HEALTHY
- 60–79 → MODERATE
- 40–59 → AT RISK
- 0–39 → CRITICAL

### Prerequisites
- Java 21
- Maven
- PostgreSQL (optional if using H2 demo mode)
- Git
- GitHub account

### Clone
```bash
git clone YOUR_GITHUB_REPOSITORY_URL
cd ProjectSphere
```

### Run
```bash
mvn clean install
mvn spring-boot:run
```

Open: http://localhost:8080

### Database Setup
Create PostgreSQL database:

```sql
CREATE DATABASE projectsphere;
```

Configure environment variables:
- DB_URL
- DB_USERNAME
- DB_PASSWORD

Example:
```bash
export DB_URL=jdbc:postgresql://localhost:5432/projectsphere
export DB_USERNAME=postgres
export DB_PASSWORD=postgres
```

If PostgreSQL is not available, the app defaults to H2 demo mode so it can still run locally.

### GitHub API Setup
Set a GitHub token:

```bash
export GITHUB_TOKEN=your_token_here
```

If no token is supplied, the system automatically falls back to demo GitHub data and keeps the intelligence features running.

> Never commit GITHUB_TOKEN or database credentials to GitHub.

### Environment Variables
Use a local `.env` file or export variables in your shell.

Example:
```bash
DB_URL=jdbc:postgresql://localhost:5432/projectsphere
DB_USERNAME=postgres
DB_PASSWORD=postgres
GITHUB_TOKEN=
```

A safe example file is included as `.env.example`.

### GitHub Workflow / CI
The repository includes GitHub Actions workflow in `.github/workflows/build.yml` that runs on push and pull_request.

### Push to GitHub
1. Create a new empty repository on GitHub named ProjectSphere.
2. Do not initialize it with a README if you already have one locally.
3. Add the remote:

```bash
git remote add origin YOUR_GITHUB_REPOSITORY_URL
```

4. Rename the branch:

```bash
git branch -M main
```

5. Push:

```bash
git push -u origin main
```

### Demo Mode
The app ships with demo mode enabled by default:

```properties
app.demo-mode=true
```

This means:
- GitHub failures do not crash the app
- demo activity is used when needed
- the dashboard remains useful during live demos

### API Endpoints
- Teams: `/api/teams`
- Users: `/api/users`
- Projects: `/api/projects`
- Tasks: `/api/tasks`
- Documents: `/api/documents`
- GitHub: `/api/projects/{projectId}/github/*`
- Intelligence: `/api/intelligence/*`
- Reports: `/api/reports/*`

### Sample Workflow
1. Open the dashboard.
2. Create a team and add members.
3. Create a project and link a repository.
4. Add tasks and documents.
5. Run the intelligence analysis.
6. Download a PDF contribution report.

### Important Files
- `pom.xml`
- `src/main/java/com/projectsphere/ProjectSphereApplication.java`
- `src/main/resources/application.properties`
- `src/main/java/com/projectsphere/intelligence/ContributionScorer.java`
- `src/main/java/com/projectsphere/intelligence/FreeRiderDetector.java`
- `src/main/java/com/projectsphere/intelligence/ProjectHealthCalculator.java`
- `src/main/java/com/projectsphere/service/IntelligenceService.java`
- `src/main/java/com/projectsphere/github/GitHubClient.java`
- `src/main/java/com/projectsphere/report/ContributionReportGenerator.java`
- `src/main/resources/static/index.html`

### Security Note
Never commit GITHUB_TOKEN or database credentials to GitHub. Store them locally in environment variables or an untracked `.env` file.

### License
This project is released under the MIT License.
