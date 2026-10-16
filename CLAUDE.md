# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

```bash
# Build
./mvnw clean package

# Run (dev)
./mvnw spring-boot:run

# Run tests
./mvnw test

# Run a single test class
./mvnw test -Dtest=QuizBaseApplicationTests
```

## Database Setup

MySQL must be running locally before starting the app. The connection is hardcoded in `src/main/java/com/example/quizBase/util/HibernateUtil.java`:
- URL: `jdbc:mysql://localhost:3306/quizbase?createDatabaseIfNotExist=true`
- Username: `root`
- Password: `12345678`

On every startup, `src/main/resources/import.sql` runs unconditionally (`spring.sql.init.mode=always`), which drops and recreates the `quizbase` database and reloads sample data. This means **all data is wiped on each restart**.

## Architecture

Standard Spring Boot MVC with a manual layered stack — no Spring Security, no Spring Data JPA repositories.

```
Thymeleaf templates (src/main/resources/templates/)
    ↓
Controllers (controller/) — receive HTTP, validate session JWT, call services
    ↓
Services (service/) — business logic, orchestrate DAO calls
    ↓
DAOs (dao/) — Hibernate Session queries via HibernateUtil.getSessionFactory()
    ↓
Entities (models/) — JPA-annotated domain objects
    ↓
MySQL (quizbase database)
```

**Hibernate is configured programmatically** in `HibernateUtil.java` — there is no `application.properties` datasource config. Every entity class must be registered there via `cfg.addAnnotatedClass(...)`.

**Authentication** is manual JWT — no Spring Security. `JwtService` generates and validates tokens (10-hour expiry). The token is stored in `HttpSession` and must be extracted and validated at the top of each protected controller method.

**DTOs** (`src/main/java/dto/`) — `QuizForm`, `QuestionForm`, `OptionForm` bind HTML form submissions and expose a `toQuiz()` / equivalent method to convert to the entity. Do not use entity classes directly as `@ModelAttribute` in forms.

## Key Relationships

- `User` → creates many `Quiz` and `Category`
- `Category` belongs to a `User` (added via `migration_add_user_to_categories.sql` — this migration is NOT in `import.sql`; run it manually once if needed)
- `Quiz` → many `Question` → many `Option`
- A quiz attempt produces one `Result` and many `Response` records (one per question)

`Quiz` uses `@BatchSize(size=10)` on its questions collection to avoid N+1 on the quiz listing pages.

## Active Development State

Quiz submission / answer evaluation was the most recently worked area (see git log). The `QuizController` and `QuizService.evaluateQuiz()` are the primary places to look when working on answer flow. The HTTP test file at `src/main/resources/crud_testing.http` can be used to test API endpoints directly.
