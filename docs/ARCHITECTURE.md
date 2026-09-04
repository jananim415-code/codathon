# ProjectSphere architecture

ProjectSphere is a single Spring Boot application with a static HTML/JavaScript
client served from the same process. The application is intentionally not split
into a second backend or a separate frontend service.

## Runtime flow

```text
Browser
  -> Spring MVC controllers (/api and static resources)
  -> services (validation, relationships, GitHub fallback, reports)
  -> Spring Data JPA repositories
  -> H2 (dev/demo) or PostgreSQL (prod)

IntelligenceService
  -> ContributionScorer / FreeRiderDetector / ProjectHealthCalculator
  -> contributions and health_score tables

GitHubService -> GitHubClient -> GitHub REST API (or deterministic demo data)
ReportService -> Apache PDFBox -> application/pdf response
```

## Main components

- `controller`: REST endpoints and response DTO mapping.
- `service`: transactional application use cases and relationship checks.
- `entity`: JPA domain model for users, teams, projects, tasks, documents,
  contributions, and health scores.
- `repository`: Spring Data JPA persistence interfaces.
- `intelligence`: deterministic, explainable scoring classes.
- `security`: BCrypt password hashing, stateless JWT authentication, and
  request authorization.
- `github`: authenticated GitHub REST calls, webhook signature verification,
  and safe demo fallback.
- `resources/db/migration`: Flyway schema migrations.

## Profiles and data

`dev` is the default profile. It uses an in-memory H2 database and loads
demonstration records through `DataInitializer`. `prod` disables seed data and
requires `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, and a base64 `JWT_SECRET`
containing at least 32 decoded bytes. Both profiles use Flyway and Hibernate
schema validation; Hibernate does not create or mutate the schema.

## Security model

Authentication endpoints, read-only `GET /api/**` requests, static resources,
the health/info actuator endpoints, and the GitHub webhook are public. All
mutating API requests (`POST`, `PUT`, and `DELETE`) require a bearer JWT.
Passwords are stored only as BCrypt hashes and user response DTOs never expose
the hash. The webhook is unauthenticated only because its HMAC-SHA256 signature
is verified when `GITHUB_WEBHOOK_SECRET` is configured.

