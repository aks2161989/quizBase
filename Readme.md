# QuizBase

QuizBase is a web application where registered users create, manage, and take quizzes across subject categories. It was built for CSYE 6220 (Enterprise Software Design) using Spring Boot, Hibernate, and server-rendered Thymeleaf pages, following the MVC and DAO patterns.

## Features

- **Accounts:** register, log in, and log out. Authentication uses JWT tokens (10-hour expiry) kept in the HTTP session.
- **Create quizzes:** a title, description, category, and any number of questions. Each question can have any number of options (at least 2) with one marked correct.
- **Edit and delete quizzes:** creators can edit their own quizzes (including adding or deleting questions and options) or delete them.
- **Categories:** browse quizzes grouped by category, and create new categories.
- **Take quizzes:** questions may be left unanswered. The result page shows the score along with the number of correct, incorrect, and unanswered questions.
- **Stored attempts:** each attempt saves a `Result` (score and date) and a `Response` for every answered question.

## Tech stack

| Layer | Technology |
|---|---|
| Language / build | Java 17, Maven (wrapper included) |
| Web | Spring Boot (Spring MVC), Thymeleaf |
| Persistence | Hibernate ORM (configured programmatically), MySQL |
| Auth | JJWT (manual JWT handling, no Spring Security) |
| Validation | Spring Boot Starter Validation |

## Architecture

```
Thymeleaf templates  (src/main/resources/templates)
        ↓
Controllers          (controller/)  HTTP handling and session/JWT checks
        ↓
Services             (service/)     business logic
        ↓
DAOs                 (dao/)         Hibernate session queries
        ↓
Entities             (models/)      User, Category, Quiz, Question, Option, Result, Response
        ↓
MySQL
```

Form submissions bind to DTOs (`QuizForm`, `QuestionForm`, `OptionForm`) rather than entities. The question and option editor shared by the create and edit pages lives in `src/main/resources/static/js/quizform.js`.

## Getting started

### Prerequisites

- JDK 17+
- MySQL running locally on port 3306

### Database

The connection settings are hardcoded in `src/main/java/com/example/quizBase/util/HibernateUtil.java`:

- URL: `jdbc:mysql://localhost:3306/quizbase?createDatabaseIfNotExist=true`
- Username: `root`
- Password: `12345678`

Change them there if your local MySQL differs.

> **Warning:** on every startup `src/main/resources/import.sql` drops and recreates the `quizbase` database and reloads sample data. All data is wiped on each restart.

Sample data includes the categories Arts, Sports, Science, History, and Entertainment, and two users (`john_doe` / `password123` and `jane_smith` / `password456`).

### Run

```bash
./mvnw spring-boot:run
```

Then open <http://localhost:8080> (the default Spring Boot port).

### Build and test

```bash
./mvnw clean package
./mvnw test
```

## Pages

| Path | Purpose |
|---|---|
| `/users/register`, `/users/login` | Sign up and sign in |
| `/dashboard` | Home page after login, with a Logout button |
| `/quizzes/listallquizzes` | Browse quizzes by category |
| `/quizzes/answer/{id}` | Take a quiz |
| `/quizzes/create` | Create a quiz |
| `/quizzes/your-quizzes` | Manage your own quizzes (edit and delete) |
| `/quizzes/edit/{id}` | Edit a quiz |
| `/categories/create` | Create a category |

`src/main/resources/crud_testing.http` and `requests.http` contain sample requests for testing the endpoints directly.

## Known limitations

- Passwords are stored in plain text, and the database credentials are hardcoded. This is acceptable for a course project but not for production.
- Users cannot yet review correct answers or browse their past results in the UI, although attempts are stored.
- Per-question answer breakdowns are shown only on the result page right after submitting, not stored.
- The UI is unstyled HTML.
