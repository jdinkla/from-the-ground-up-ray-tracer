# 2. Architecture Constraints

This chapter describes the constraints that influenced the architectural decisions for the ray tracer project. These constraints were derived from analyzing the project's configuration files, build scripts, and code structure.

## 2.1 Technical Constraints

| Constraint | Explanation |
|------------|-------------|
| **Kotlin/JVM 25** | The project uses Kotlin as the sole programming language, targeting JVM 25 as specified in `build.gradle.kts` via `jvmToolchain(25)`. This determines the available language features and runtime capabilities. |
| **Gradle Build System** | Gradle is the build tool with version 9.2.0, using the Kotlin DSL for build configuration. Dependencies are managed through Maven Central with version catalogs. |
| **Kotlinx Coroutines** | Asynchronous rendering operations rely on `kotlinx.coroutines.core`, constraining concurrent execution patterns to Kotlin's coroutine model. |
| **Korim Image Library** | Image processing and output use the `korim` library from Korlibs, which determines supported image formats and rendering pipeline capabilities. |
| **JVM Memory Configuration** | The JVM is configured with 8GB maximum heap (`-Xmx8192m` in `gradle.properties`), reflecting the memory-intensive nature of ray tracing operations with large scenes and meshes. |
| **Logback Logging** | Logging infrastructure uses `logback-classic`, requiring SLF4J-compatible logging patterns throughout the codebase. |
| **Clikt CLI Framework** | Command-line interface is implemented using Clikt, constraining how command-line arguments are parsed and validated. |
| **ClassGraph Reflection** | Runtime class discovery for scene files uses ClassGraph, enabling dynamic loading of scene definitions from the examples package. |
| **JUnit 5 / Kotest** | Testing framework is Kotest with JUnit 5 platform integration, determining test structure and assertion styles. |

## 2.2 Organizational Constraints

| Constraint | Explanation |
|------------|-------------|
| **Apache License 2.0** | The project is licensed under Apache License 2.0, which permits commercial use, modification, and distribution while requiring preservation of copyright notices and disclaimers. Contributors must accept these licensing terms. |
| **GitHub Actions CI** | Continuous integration runs on Ubuntu with JDK 25 (Temurin distribution). All pull requests to `main` must pass the Gradle build, and failed test reports are uploaded as artifacts. |
| **Dependency Submission** | The CI pipeline automatically submits dependency graphs to GitHub for security vulnerability tracking, requiring all dependencies to be declared through Gradle. |
| **Single Main Branch** | The workflow triggers on pushes and pull requests to the `main` branch only, indicating a trunk-based development model. |
| **JDK Distribution** | CI enforces Temurin (Eclipse Adoptium) as the JDK distribution, ensuring consistent build behavior across development and CI environments. |
| **Personal/Educational Project** | Based on the README history (originating from Kevin Suffern's "Ray Tracing from the Ground Up" book), this is an educational implementation. Changes should preserve the pedagogical clarity of the ray tracing algorithms. |

## 2.3 Conventions

| Convention | Description |
|------------|-------------|
| **Official Kotlin Code Style** | The `kotlin.code.style=official` setting in `gradle.properties` enforces Kotlin's official formatting conventions across the codebase. |
| **Detekt Static Analysis** | Code quality is enforced via Detekt with a custom configuration (`detekt-config.yml`). Maximum 150 issues are tolerated, with active rules for complexity, naming, performance, and potential bugs. |
| **Max Line Length: 120** | Source files must not exceed 120 characters per line, as enforced by Detekt's `MaxLineLength` rule. |
| **Cyclomatic Complexity ≤ 15** | Methods with cyclomatic complexity above 15 are flagged, encouraging decomposition of complex algorithms. |
| **Method Length ≤ 60 lines** | Methods exceeding 60 lines trigger warnings, promoting smaller, focused functions. |
| **Class Size ≤ 600 lines** | Large classes are flagged to encourage proper separation of concerns. |
| **Parameter Lists ≤ 6/7** | Functions may have at most 6 parameters; constructors may have 7. This limits complexity in method signatures. |
| **PascalCase Class Names** | Classes follow the pattern `[A-Z][a-zA-Z0-9]*` (e.g., `Sphere`, `GlossySpecular`, `KDTree`). |
| **camelCase Functions/Variables** | Functions and variables use `[a-z][a-zA-Z0-9]*` pattern (e.g., `shadowHit`, `maxToOne`). |
| **SCREAMING_SNAKE_CASE Constants** | Top-level constants use `[A-Z][_A-Z0-9]*` pattern (e.g., `K_EPSILON`, `BLACK`, `WHITE`). |
| **Interface Naming with I-Prefix** | Interfaces use a leading `I` prefix (e.g., `IHit`, `ILens`, `IMaterial`, `IGeometricObject`), following a C#/Java enterprise convention. |
| **Package Structure** | Packages follow `net.dinkla.raytracer.<domain>` hierarchy, organizing code by ray tracing concepts (math, objects, materials, lights, cameras, etc.). |
| **Data Classes for Value Types** | Immutable value objects use Kotlin `data class` (e.g., `Color`, `Point3D`, `Vector3D`), providing automatic `equals`, `hashCode`, and `toString`. |
| **Operator Overloading for Math** | Mathematical operations on vectors, points, and colors use operator overloading (`+`, `-`, `*`, `dot`) for natural mathematical notation. |
| **No Wildcard Imports** | Wildcard imports are prohibited except for `java.util.*`, encouraging explicit dependency declarations. |
| **No TODO/FIXME Comments** | Detekt's `ForbiddenComment` rule flags `TODO:`, `FIXME:`, and `STOPSHIP:` markers, requiring issues to be tracked externally rather than in code comments. |
| **Return Count ≤ 5** | Functions should have at most 5 return statements to maintain readable control flow. |
| **JaCoCo Coverage Reports** | Test coverage is tracked via JaCoCo with XML and HTML reports generated, excluding examples and UI code from coverage metrics. |