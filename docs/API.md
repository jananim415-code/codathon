# ProjectSphere API

The application listens on `http://localhost:8080`. JSON requests should send
`Content-Type: application/json`. `GET` API operations are public; write
operations require `Authorization: Bearer <token>`.

## Authentication

| Method | Path | Purpose |
|---|---|---|
| POST | `/api/auth/register` | Create a student account and return a JWT |
| POST | `/api/auth/login` | Authenticate and return a JWT |

Registration accepts `name`, `email`, `password` (8–72 characters), and
optional `githubUsername`. The response includes `token`, `tokenType`,
`expiresIn`, and a password-free `user`.

## Core resources

| Method | Path | Purpose |
|---|---|---|
| GET/POST | `/api/users` | List/create users |
| GET/PUT | `/api/users/{id}` | Read/update a user |
| GET/POST | `/api/teams` | List/create teams |
| GET | `/api/teams/{id}` | Read a team |
| POST/DELETE | `/api/teams/{teamId}/members/{userId}` | Manage membership |
| GET/POST | `/api/projects` | List/create projects |
| GET/PUT | `/api/projects/{id}` | Read/update a project |
| GET | `/api/projects/{id}/stats` | Project statistics |
| GET/POST | `/api/tasks` | List/create tasks |
| GET | `/api/projects/{projectId}/tasks` | Tasks for a project |
| PUT/DELETE | `/api/tasks/{id}` | Update/delete a task |
| GET/POST | `/api/documents` | List/create documents |
| GET | `/api/projects/{projectId}/documents` | Documents for a project |
| PUT/DELETE | `/api/documents/{id}` | Update/delete a document |

Create/update payloads use the corresponding resource fields plus relationship
IDs (`teamId`, `projectId`, `assignedUserId`, or `createdById`). Enum values are
the Java names: project status `ACTIVE|COMPLETED|AT_RISK|STALLED`, task status
`TODO|IN_PROGRESS|COMPLETED`, task priority `LOW|MEDIUM|HIGH`.

## Dashboard, GitHub, intelligence, and reports

| Method | Path | Purpose |
|---|---|---|
| GET | `/api/dashboard` | Live counts, task completion, and project health |
| GET | `/api/projects/{id}/github/commits` | GitHub commits or demo fallback |
| GET | `/api/projects/{id}/github/pulls` | GitHub pull requests or demo fallback |
| GET | `/api/projects/{id}/github/activity` | Recent repository activity |
| GET | `/api/github/demo-status` | Whether demo data is active |
| POST | `/api/github/webhook` | Verify and acknowledge a GitHub webhook |
| GET | `/api/intelligence/projects/{id}/contributions` | Member contribution metrics |
| GET | `/api/intelligence/projects/{id}/free-riders` | Flagged potential low contributors |
| GET | `/api/intelligence/projects/{id}/health` | Calculate and persist project health |
| GET | `/api/intelligence/projects/{id}/summary` | Full intelligence summary |
| POST | `/api/intelligence/analyze/{id}` | Run analysis for one project |
| POST | `/api/intelligence/analyze-all` | Run analysis for all projects |
| GET | `/api/reports/project/{id}` | Download a project PDF |
| GET | `/api/reports/project/{projectId}/user/{userId}` | Download a user PDF |

## Operational endpoints

`GET /actuator/health` and `GET /actuator/info` are exposed. Metrics are also
exposed at `GET /actuator/metrics`; detailed health data is restricted by
Spring Security.

Errors use a JSON body containing `timestamp`, `status`, and `message`, with
field `details` for validation failures. Missing resources return 404,
duplicate/conflicting data 409, invalid input 400, and failed authentication
401.

The health and summary GET endpoints refresh and persist the current health
calculation; all other GET endpoints are read-only.
