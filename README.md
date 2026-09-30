# Forum

A server-rendered forum web application built with Spring Boot as a university project.

## Features

- Registration, login and logout with BCrypt-hashed passwords
- Public feed of publications, readable without logging in
- Creating, editing and deleting publications; only the author can modify a publication
- Russian and English interface with a language switcher
- On-demand translation of publication titles and content via LibreTranslate

## Tech stack

Java 21, Spring Boot 4.1 (Spring MVC, Spring Security, Spring Data JPA), Thymeleaf, PostgreSQL 16, Flyway, LibreTranslate, Docker Compose.

## Getting started

Requires Docker with Docker Compose.

```bash
docker compose up --build
```

The application is available at http://localhost:8080.

On first start, LibreTranslate downloads its language models, which takes a few minutes. The forum works in the meantime, and translation becomes available once the download finishes. To follow the progress, run `docker compose logs -f libretranslate`.

## Usage

Anyone can read the feed and publications. Sign up to create publications; the author sees Edit and Delete buttons on their own publications. Switch the interface language with the RU and EN links in the header, and click Translate on the feed or a publication page to translate the text into the current language.

## Development

Requires JDK 21. Start the database and the translator in Docker, then run the application from the IDE (`ForumApplication`) or with the Maven Wrapper:

```bash
docker compose up -d postgres libretranslate
./mvnw spring-boot:run
```

On Windows, use `mvnw.cmd` instead of `./mvnw`.

Tests start the application context and need a running database:

```bash
./mvnw test
```

## Configuration

Settings are defined in `src/main/resources/application.yaml` and can be overridden with environment variables.

| Property | Environment variable | Default |
|---|---|---|
| `spring.datasource.url` | `SPRING_DATASOURCE_URL` | `jdbc:postgresql://localhost:5445/forum` |
| `spring.datasource.username` | `SPRING_DATASOURCE_USERNAME` | `forum` |
| `spring.datasource.password` | `SPRING_DATASOURCE_PASSWORD` | `forum` |
| `translation.url` | `TRANSLATION_URL` | `http://localhost:5000` |

Local ports: application `8080`, PostgreSQL `5445`, LibreTranslate `5000`.

## Project structure

```
src/main/java/ru/kpfu/forum
├── config        Spring Security and interface language
├── controller    HTTP request handling
├── dto           form and translation objects
├── entity        JPA entities
├── repository    data access
└── service       business logic, author checks, translation

src/main/resources
├── db/migration          Flyway migrations
├── templates             Thymeleaf templates
├── static                stylesheet
└── messages*.properties  interface texts
```

The database schema is created and changed only by Flyway migrations; Hibernate just validates the entities against it (`ddl-auto: validate`).

## Contributing

- `main` holds the stable version, `develop` is the integration branch, and each task gets its own `feature/*` branch
- Commit messages follow [Conventional Commits](https://www.conventionalcommits.org/)
- Branches are merged into `develop` through pull requests with a merge commit, without squashing
