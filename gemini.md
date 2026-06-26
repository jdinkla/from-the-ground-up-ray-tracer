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
*   This is a **JVM-only** project (`kotlin("jvm")`, JDK 25). The `src` tree uses
    Kotlin-Multiplatform-style source sets (`commonMain`/`jvmMain`/`examples`) as a vestige of an
    abandoned multiplatform experiment — they are wired up manually in `build.gradle.kts`. Treat
    `commonMain` as the platform-independent rendering core and `jvmMain` as JVM-specific I/O, the
    Swing UI, and the parallel renderers. (See `CLAUDE.md` for the authoritative description.)
*   A DSL is used for defining scenes, located in `src/commonMain/kotlin/net/dinkla/raytracer/world/dsl`.
