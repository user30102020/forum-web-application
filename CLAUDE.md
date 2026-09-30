# CLAUDE.md

Форум на Spring Boot 4: регистрация, лента публикаций, интерфейс на двух языках, перевод публикаций. Запуск и конфигурация описаны в `README.md`.

## Команды

- Сборка: `./mvnw clean compile` (на Windows — `mvnw.cmd`)
- Тесты: `./mvnw test`; перед ними поднять базу — `docker compose up -d postgres`
- После изменений кода запускать сборку и тесты

## Spring Boot 4 и Spring Security 7

Примеры под Spring Boot 3 здесь не работают.

- Веб-стартер — `spring-boot-starter-webmvc`, не `spring-boot-starter-web`
- Flyway — `spring-boot-starter-flyway` и `flyway-database-postgresql`
- Security 7: только лямбда-DSL; `.and()` и `WebSecurityConfigurerAdapter` не существуют
- Диалекта `sec:` в Thymeleaf нет: данные о пользователе и правах передавать в модель
- JSON — Jackson 3; аннотации из `com.fasterxml.jackson.annotation`
- Автоконфигурации `RestClient.Builder` нет: клиент создаётся через `RestClient.builder()`

## Архитектура

- Controller → Service → Repository; контроллеры не обращаются к репозиториям
- Права автора проверяются в сервисе (`PublicationService.getByIdAndCheckAuthor`), а не только скрытием кнопок в шаблоне
- Формы биндятся в классы из `dto`, никогда в сущности
- В сервисах `find*` может вернуть пустой результат, `get*` возвращает объект или бросает исключение
- «Не найдено» — `ResponseStatusException(HttpStatus.NOT_FOUND)`, «нет прав» — `AccessDeniedException`
- Зависимости внедряются через конструктор с `@RequiredArgsConstructor`

## База данных

- Изменение схемы — только новый файл `V{N}__{описание}.sql` в `src/main/resources/db/migration`, вместе с правкой сущности
- Применённые миграции не изменять, не переименовывать и не переформатировать: Flyway сверяет контрольные суммы
- `ddl-auto: validate` не менять
- Связи `LAZY`; при `open-in-view: false` нужные шаблону связи загружать через `join fetch` в `@Query`
- На сущностях из Lombok только `@Getter` и `@Setter`
- `publications.updated_at = null` означает «не редактировалась»; значение ставит только `@PreUpdate`
- Время — `LocalDateTime` в поясе Europe/Moscow, он же задан контейнеру в `docker-compose.yml`

## Веб-слой

- Изменяющие действия — POST-формы с `th:action` (он добавляет CSRF-токен), а не ссылки
- Текущий пользователь в шаблонах — `${currentUsername}` из `CurrentUserAdvice`
- Новые публичные адреса добавлять в `SecurityConfig`; `/error` должен оставаться открытым
- Вёрстка минимальная: голый HTML, один `static/css/style.css`, без CSS-фреймворков, иконок и JavaScript

## Локализация

- Текст интерфейса — ключи в `messages.properties` (русский) и `messages_en.properties`; новый ключ добавлять в оба файла, кодировка UTF-8
- Формат даты — ключ `date.format`
- Ошибки для пользователя сервис сообщает кодом сообщения, а не текстом (`UserService.register`)
- `spring.messages.fallback-to-system-locale: false` не убирать: иначе язык по умолчанию зависит от ОС

## Перевод публикаций

- Перевод не сохранять ни в базе, ни в сущности
- Каждый текст переводить отдельным запросом: в пакетном запросе LibreTranslate определяет язык один раз на весь массив
- Недоступность переводчика не должна ломать страницу: показывать оригинал
- Сервис `app` намеренно не зависит от `libretranslate` в `docker-compose.yml`

## Код и Git

- Никаких комментариев в коде
- Ветки `feature/*` от `develop`, слияние через PR обычным merge-коммитом, без squash
- Conventional Commits: `feat:`, `fix:`, `refactor:`, `test:`, `docs:`, `chore:`; один коммит на завершённый этап
