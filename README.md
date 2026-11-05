# Индивидуальное задание по backend-разработке

Приложение для работы с пропусками посетителей. Проект написан на Kotlin с использованием Spring Boot и PostgreSQL. HTTP API и структура базы данных подготовлены; условия заданий находятся в [TASK.md](TASK.md).

## Запуск

Установите JDK 17, Gradle 8.14.4 и Docker Compose. Gradle wrapper в проекте не предоставляется. Для первой сборки нужен доступ к Maven Central, для загрузки образа PostgreSQL — к Docker Hub.

Выполните команды в каталоге проекта:

```bash
docker compose -f compose.yml up -d
docker compose -f compose.yml exec -T postgres pg_isready -U nto -d nto_individual
gradle bootRun
```

Если база данных ещё не готова, повторите команду `pg_isready`. По умолчанию приложение подключается к базе `nto_individual` на `localhost:5434` с пользователем и паролем `nto`. При необходимости задайте подключение через переменные `DB_URL`, `DB_USER` и `DB_PASSWORD`.

Приложение доступно на порту 8080. Порт можно изменить переменной `SERVER_PORT`. Для тестов и запросов получения кода используйте только отдельную учебную базу: её данные могут изменяться.
