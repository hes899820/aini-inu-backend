# Aini Inu Backend Context

## Project Overview
**Aini Inu** is a social platform for dog walkers, facilitating socialization for both pets and owners. This repository contains the backend implementation using Spring Boot.

- **Primary Language:** Java 21
- **Framework:** Spring Boot 3.5.10
- **Build Tool:** Gradle
- **Database:** MySQL 9+ (with Vector support for AI features), H2 (for simple testing)
- **Architecture:** Layered Architecture (Controller -> Service -> Repository)

## Key Features
1.  **Member & Pet Management:** Profiles for owners and non-owners, pet registration with public data verification.
2.  **Walking Threads:** Recruitment for walking companions with location and time-based filtering.
3.  **Chat System:** Real-time messaging for coordination (Individual & Group).
4.  **Lost Pet Finder:** AI-powered search using multi-modal embeddings (CLIP) and vector storage.
5.  **Community:** General feed for sharing pet daily lives.

## Development Conventions
- **Package Structure:** `scit.ainiinu`
- **Response Format:** All APIs return a standardized `ApiResponse<T>` wrapper.
- **Exception Handling:** Centralized via `GlobalExceptionHandler` using custom `ErrorCode` enums.
- **Entities:** Use `BaseTimeEntity` for auditing (createdAt, updatedAt).
- **DTOs:** Request/Response DTOs are used for all API interactions; Entities are not exposed directly.

## Building and Running
- **Build:** `./gradlew build`
- **Run:** `./gradlew bootRun`
- **Test:** `./gradlew test`

## Documentation & Skills
- **Specifications:** Detailed specs are located in `spec-docs/`.
    - `spec_v4.2.md`: General Planning & Requirements
    - `backend_spec_03_api.md`: API Specification
- **Gemini Skills:** Specialized agents are available in `.gemini/skills/` to assist with:
    - `spring-backend-generator`: Generating boilerplate code (Entity, DTO, Controller, Service).
    - `spring-test-generator`: Creating Unit and Integration tests.
    - `api-spec-validator`: Validating implementation against API specs.
    - `spec-sync-validator`: Ensuring consistency across spec documents.

## Important Files
- `src/main/java/scit/ainiinu/AiniInuApplication.java`: Main entry point.
- `src/main/resources/application.properties`: Configuration.
- `build.gradle`: Project dependencies.
