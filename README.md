# Task Management Service

![Java](https://img.shields.io/badge/java-17%2B-blue)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.3-brightgreen)

REST API сервис для управления задачами с автоматическим переходом статусов. Реализован на Spring Boot 3 без использования базы данных.

## Основные возможности

- Создание задач с указанием описания и времени выполнения
- Автоматический переход задач в статус DONE по истечении времени
- Отмена задач в процессе выполнения
- Просмотр списка задач и отдельных задач по ID
- Потокобезопасная реализация (поддержка многопоточной среды)

## Технологический стек

- **Java 17**
- **Spring Boot 3.2**
- **Gradle** (система сборки)
- **JUnit 5** (тестирование)
- **Awaitility** (тестирование асинхронных операций)

## REST API

POST	/api/v1/tasks	Создание новой задачи	{description: string, duration: number}

GET	/api/v1/tasks	Получение списка всех задач

GET	/api/v1/tasks/{id}	Получение задачи по ID

DELETE	/api/v1/tasks/{id}	Отмена задачи (смена статуса на CANCELED)

## Архитектура проекта

```text
src/
├── main/
│   ├── java/
│   │   └── resenkov/
│   │       └── work/
│   │           └── t1taskmanager/
│   │               ├── config/            # Swagger-config
│   │               ├── controller/        # REST контроллеры
│   │               ├── model/             # Модели данных (Task)
│   │               ├── repository/        # Хранилище задач в памяти
│   │               ├── service/           # Бизнес-логика
│   │               └── TaskManagerApplication.java # Главный класс
│   └── resources/                 
└── test/
    └── java/
        └── resenkov/
            └── work/
                └── t1taskmanager/
                    ├── service/
                        └── TaskServiceIntegrationTest.java    # Интеграционные тесты
                    └── BaseIntegrationTest.java # Базовый класс для тестов
```
## Postman
Прилагается коллекция [Postman](https://github.com/Resenkov/t1-task-manager/blob/master/t1-task-manager.postman_collection.json) , но в проекте так же реализован [Swagger](http://localhost:8080/swagger-ui.html).
