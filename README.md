# Users

A Reactive application showcasing various operations using the Spring
WebFlux framework.

------------------------------------------------------------------------

## Overview

This project demonstrates how to build a fully reactive REST API using
**Spring WebFlux** and **R2DBC**. It focuses on non-blocking,
asynchronous data processing and reactive streams using Project Reactor.

The application implements CRUD operations on a `User` resource while
following reactive programming principles.

------------------------------------------------------------------------

## ️ Tech Stack

-   Java 21
-   Spring Boot
-   Spring WebFlux
-   Spring Data R2DBC
-   H2 / PostgreSQL (Reactive Driver)
-   Project Reactor (Mono / Flux)
-   JUnit 5 & Reactor Test
-   Mockito

------------------------------------------------------------------------

## Features

-   Fully reactive REST APIs\
-   Non-blocking database interaction\
-   Streaming endpoints (Server-Sent Events)\
-   Pagination support\
-   Validation and error handling\
-   Unit and integration testing\
-   Layered architecture

------------------------------------------------------------------------

## Running the Application

``` bash
git clone https://github.com/ritujane78/users_reactive.git
cd users_reactive
./mvnw spring-boot:run
```

------------------------------------------------------------------------

## API Endpoints

| Method | Endpoint       | Description        |
|--------|----------------|------------------|
| GET    | /users         | Get all users     |
| GET    | /users/{id}    | Get user by ID    |
| POST   | /users         | Create user       |
| GET    | /users/stream  | Stream users (SSE)|


------------------------------------------------------------------------

## Testing

``` bash
./mvnw test
```

Includes unit, and reactive stream testing.

