# Аренда вещей - веб-сервис на Java Spring Boot
Стек - Java, Spring Boot, PostgreSQL, Hibernate ORM, Maven, Lombok, Docker, REST API, Mockito, JUnit.

## О проекте
Пользователи могут арендовать чужие вещи и сдавать в аренду свои, делать запросы на вакантные вещи, осуществлять поиск вещей по параметрам, комментировать завершенную аренду.

## Основная функциональность
- POST /users - добавление пользователя
- PATCH /users/{userId} - обновление данных пользователя
- GET /users/{userId} - получение данных пользователя
- GET /users/ - получение списка пользователей
<!-- -->
- POST /items - добавление вещи
- PATCH /items/{itemId} - обновление данных вещи
- GET /items/{itemId} - получение данных вещи
- GET /items/ - получение списка вещей
- GET /items/search - поиск вещей по тексту в параметре text
- POST /items/{itemId}/comment - добавление отзыва к вещи после завершенного бронирования
<!-- -->
- POST /requests - добавление запроса на бронирование
- GET /requests/{requestId} - получение бронирования
- GET /items/all - получение списка бронирований
- GET /items - получение списка бронирований по id пользователя в заголовке запроса
<!-- -->
- PATCH /bookings/{bookingId} - обновление данных бронирования
- PATCH /bookings/{bookingId} - одобрение или отклонение бронирования по параметру approved
- GET /bookings/{bookingId} - получение данных о бронировании
- GET /bookings/ - получение бронирований по фильтрам state, from, size
- GET /bookings/owner - получение бронирований пользователя по фильтрам state, from, size

## Микросервисная архитектура
Docker контейнеры под каждый микросервис:
* Gateway - для валидации запросов
* Server - содержит бизнес-логику
* PostgreSQL - база данных

## Схема БД
![](https://github.com/gandistip/renting-things/blob/a796dbdade740c45742a9bb65801191ca8e5566c/server/src/main/resources/dbSchema.png)

## Postman тесты
https://github.com/gandistip/renting-things/blob/main/postman/tests.json
