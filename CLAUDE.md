# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

```bash
# Build Backend
./mvnw clean package

# Run Spring Boot (dev)
./mvnw spring-boot:run

# Run tests
./mvnw test

# Run a single test class
./mvnw test -Dtest=QuizRestControllerTest

# Angular Frontend (under frontend/)
cd frontend
npm start        # Runs Angular on http://localhost:4200
npm run build    # Production build into frontend/dist/frontend
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

- **Phase 1 (Security & Architecture)**: BCrypt password hashing, RBAC, externalized properties, centralized `AuthInterceptor`, and unit tests completed.
- **Phase 1.5 (Modern Angular 18 SPA)**: Built Angular 18 frontend in `frontend/` with Bootstrap 5.3 + Bootstrap Icons. Implemented REST API controllers (`AuthRestController`, `QuizRestController`, `CategoryRestController`, `DashboardRestController`) and DTOs (`com.example.quizBase.dto`). Added CORS and preflight handling. All 17 unit tests pass.
- **Next Phase**: Phase 2 (Enterprise Patterns & Quality: `@ControllerAdvice` global error handling, AOP performance monitoring & auditing, and extended MockMvc test suite).

## AI Assistant Directives

> **Important Directive:** Whenever the user asks "what is next?" or asks what to do next, you MUST remind the user to test the frontend thoroughly first (testing login/register, quiz taking and scoring, quiz creation/editing, and categories) before proceeding to Phase 2 tasks.
