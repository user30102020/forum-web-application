# Forum

Учебное веб-приложение — форум на Spring Boot.

## Возможности

- Регистрация, вход и выход; пароли хранятся в виде BCrypt-хэшей
- Общая лента публикаций, доступная без входа
- Создание, редактирование и удаление публикаций; изменять публикацию может только её автор
- Интерфейс на русском и английском языках с переключателем
- Перевод заголовков и текстов публикаций на язык интерфейса через LibreTranslate

## Технологии

Java 21, Spring Boot 4.1 (Spring MVC, Spring Security, Spring Data JPA), Thymeleaf, PostgreSQL 16, Flyway, LibreTranslate, Docker Compose.

## Запуск

Требуется Docker.

```bash
docker compose up --build
```

Приложение будет доступно по адресу http://localhost:8080

При первом запуске LibreTranslate несколько минут скачивает языковые модели. Форум в это время уже работает, перевод станет доступен после загрузки. Ход загрузки: `docker compose logs -f libretranslate`.

## Разработка

Требуется JDK 21. База данных и переводчик запускаются в Docker, приложение — из IDE (класс `ForumApplication`) или через Maven Wrapper:

```bash
docker compose up -d postgres libretranslate
./mvnw spring-boot:run
```

На Windows вместо `./mvnw` используется `mvnw.cmd`.

Тесты поднимают контекст приложения и требуют запущенной базы данных:

```bash
./mvnw test
```

## Конфигурация

Настройки задаются в `src/main/resources/application.yaml` и переопределяются переменными окружения.

| Настройка | Переменная окружения | По умолчанию |
|---|---|---|
| `spring.datasource.url` | `SPRING_DATASOURCE_URL` | `jdbc:postgresql://localhost:5445/forum` |
| `spring.datasource.username` | `SPRING_DATASOURCE_USERNAME` | `forum` |
| `spring.datasource.password` | `SPRING_DATASOURCE_PASSWORD` | `forum` |
| `translation.url` | `TRANSLATION_URL` | `http://localhost:5000` |

Порты на локальной машине: приложение — `8080`, PostgreSQL — `5445`, LibreTranslate — `5000`.

## Структура

```
src/main/java/ru/kpfu/forum
├── config        — Spring Security и выбор языка интерфейса
├── controller    — обработка HTTP-запросов
├── dto           — объекты форм и перевода
├── entity        — JPA-сущности
├── repository    — доступ к базе данных
└── service       — бизнес-логика, проверка авторства, перевод

src/main/resources
├── db/migration          — миграции Flyway
├── templates             — шаблоны Thymeleaf
├── static                — стили
└── messages*.properties  — тексты интерфейса
```

Схема базы данных создаётся и изменяется только миграциями Flyway; Hibernate лишь проверяет соответствие сущностей схеме (`ddl-auto: validate`).