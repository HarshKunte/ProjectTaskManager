# Task Manager & Collaboration app

A REST API for managing projects, tasks, comments, assignees, and file attachments. It uses Spring Boot 4, Spring Security with JWT authentication, PostgreSQL, JPA, and Gradle.

## Prerequisites

- Java 21
- Docker and Docker Compose
- Git

## Setup

1. Clone the project and open it in a terminal:

```bash
cd taskmanager
```

2. Start PostgreSQL:

```bash
docker-compose up -d
```

PostgreSQL is available at `localhost:5432` using:

- Database: `taskdb`
- Username: `taskuser`
- Password: `taskpass`

The default database and storage settings are in `src/main/resources/application.properties`. Uploaded files are stored in `uploads/`.

## Run

Windows:

```bat
gradlew.bat bootRun
```

Linux/macOS:

```bash
./gradlew bootRun
```

The API runs at `http://localhost:8080`.

Stop PostgreSQL when finished:

```bash
docker-compose down
```

## Authentication

1. Sign up or log in to obtain a JWT.
2. Send it in protected requests:

```http
Authorization: Bearer <token>
```

Example signup:

```bash
curl -X POST http://localhost:8080/auth/signup \
  -H "Content-Type: application/json" \
  -d '{"email":"user@example.com","password":"password123","fullName":"Test User"}'
```

Example login:

```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"user@example.com","password":"password123"}'
```

## API Endpoints

| Method | Endpoint | Description | Authentication |
|---|---|---|---|
| `POST` | `/auth/signup` | Register a user | No |
| `POST` | `/auth/login` | Log in and receive a JWT | No |
| `POST` | `/auth/logout` | Revoke the current token | Bearer token |
| `GET` | `/users/me` | Get the current user | Bearer token |
| `PUT` | `/users/me` | Update the current user's profile | Bearer token |
| `POST` | `/projects` | Create a project | Bearer token |
| `GET` | `/projects` | List the user's projects | Bearer token |
| `GET` | `/projects/{id}` | Get project details and members | Bearer token |
| `POST` | `/projects/{id}/members` | Add a member | Bearer token |
| `DELETE` | `/projects/{id}/members/{userId}` | Remove a member | Bearer token |
| `POST` | `/api/v1/projects/{pid}/tasks` | Create a task | Bearer token |
| `GET` | `/api/v1/projects/{pid}/tasks` | List project tasks | Bearer token |
| `GET` | `/api/v1/tasks/mine` | List tasks assigned to the current user | Bearer token |
| `GET` | `/api/v1/tasks/{id}` | Get task details | Bearer token |
| `PUT` | `/api/v1/tasks/{id}` | Update a task | Bearer token |
| `PATCH` | `/api/v1/tasks/{id}/assignee` | Assign a task | Bearer token |
| `PATCH` | `/api/v1/tasks/{id}/status` | Change task status | Bearer token |
| `DELETE` | `/api/v1/tasks/{id}` | Delete a task | Bearer token |
| `POST` | `/api/v1/tasks/{taskId}/comments` | Add a comment | Bearer token |
| `GET` | `/api/v1/tasks/{taskId}/comments` | List comments | Bearer token |
| `DELETE` | `/api/v1/tasks/{taskId}/comments/{id}` | Delete a comment | Bearer token |
| `POST` | `/api/v1/tasks/{taskId}/attachments` | Upload an attachment (`multipart/form-data`, field: `file`) | Bearer token |
| `GET` | `/api/v1/tasks/{taskId}/attachments` | List task attachments | Bearer token |
| `GET` | `/api/v1/attachments/{id}/download` | Download an attachment | Bearer token |
| `DELETE` | `/api/v1/attachments/{id}` | Delete an attachment | Bearer token |

## Run Tests

```bash
gradlew.bat test
```

On Linux/macOS:

```bash
./gradlew test
```
