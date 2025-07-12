# Gemini Project Context

## About the Project

This is a ray tracer written in Kotlin, based on the book "Ray Tracing from the Ground Up". It uses a Kotlin-based DSL to define 3D scenes.

## Commands

*   **Build:** `./gradlew build` (builds the project, runs tests and static analysis)
*   **Test:** `./gradlew test` (runs the unit tests)
*   **Lint:** `./gradlew detekt` (runs the detekt static analysis tool)
*   **Run (GUI):** `./gradlew swing` (launches the Swing GUI)
*   **Run (CLI):** `./gradlew run --args="--world=<world_file> --renderer=<renderer_type> --resolution=<resolution>"` (runs the ray tracer from the command line)
*   **Update Dependencies:** `./gradlew refreshVersions` (updates the project's dependencies)

## Conventions

*   The project uses Gradle for building and dependency management.
*   Code style is enforced by `detekt` using the configuration in `detekt-config.yml`.
*   The project is structured as a Kotlin multi-platform project, with the main logic in `src/commonMain/kotlin`.
*   A DSL is used for defining scenes, located in `src/commonMain/kotlin/net/dinkla/raytracer/world/dsl`.
