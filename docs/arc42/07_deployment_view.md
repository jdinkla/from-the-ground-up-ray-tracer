# 7. Deployment View

The ray tracer is a **standalone desktop / command-line application** with a source-based
deployment model. It is built and run from a developer workstation or in CI; there is no
server, container, or cloud component.

## 7.1 Infrastructure overview

```
  +---------------------------- Developer workstation ----------------------------+
  |  JDK 25 (Temurin)                                                             |
  |    +-- Gradle wrapper (./gradlew)  --build/test/detekt/coverage-->  reports   |
  |    +-- just (task runner)          --wraps gradle + render shortcuts          |
  |    +-- java -jar / gradle run/swing --renders--> ../*.png                     |
  |    +-- resources/*.ply (mesh input)                                           |
  +------------------------------------------------------------------------------+

  +------------------------------ GitHub Actions --------------------------------+
  |  ubuntu-latest, JDK 25 (Temurin)                                             |
  |    push/PR to main -> ./gradlew build (compile + test + detekt)              |
  |    on failure -> upload build/reports + test-results as artifacts            |
  |    dependency-submission job -> dependency graph to GitHub                   |
  +-----------------------------------------------------------------------------+
```

## 7.2 Infrastructure elements

| Element | Description |
|---------|-------------|
| **JVM runtime** | JDK 25 (Temurin in CI). `jvmToolchain(25)` in `build.gradle.kts` is the authoritative version; the GUI and CLI run on the same runtime |
| **Build environment** | Gradle (Kotlin DSL); the wrapper pins the Gradle version. `gradle.properties` sets the JVM heap (`-Xmx8192m`) for memory-heavy mesh scenes |
| **Task runner** | `justfile` provides workflow shortcuts (`just build`, `just test`, `just run`, `just swing`, `just coverage`) over the underlying Gradle tasks |
| **Convenience scripts** | `bin/*.sh` (`1080p.sh`, `area1080p.sh`, `benchmark.sh`, `swing.sh`) wrap common render/benchmark invocations |
| **CI/CD** | GitHub Actions workflow `gradle.yml`: a `build` job (compile + test + Detekt) and a `dependency-submission` job, both on JDK 25 |
| **Inputs/outputs** | Mesh inputs under `resources/` (some bundled, some downloaded — see `README.md`); PNG images written relative to the working directory |

## 7.3 Mapping building blocks to infrastructure

| Building block | Runs in | Notes |
|----------------|---------|-------|
| Rendering core (`commonMain`) | JVM process | Platform-independent; exercised by tests and both front ends |
| Parallel renderers, I/O, scene host (`jvmMain`) | JVM process | JVM threads, virtual threads, coroutines; file/PNG I/O |
| Swing GUI | JVM process (desktop) | `./gradlew swing` / `bin/swing.sh` |
| CLI | JVM process (headless ok) | `./gradlew run --args="…"` / `bin/*.sh` |
| Tests + quality gates | Gradle / CI | Kotest, JaCoCo, Detekt |

## 7.4 Runtime configuration

- **Renderer** and **resolution** are chosen per invocation (`--renderer`, `--resolution`); see
  [Chapter 6](06_runtime_view.md).
- **Heap**: large scenes (dense grids, big meshes) rely on the 8 GB max heap configured in
  `gradle.properties`; the standalone `audit` task raises this further.
- **Reports**: JaCoCo HTML/XML under `build/reports/jacoco`, Detekt under `build/reports/detekt`,
  test results under `build/test-results`.

## 7.5 Infrastructure decisions

The project deliberately has **no containerization and no cloud deployment**: it is a
CPU-bound, single-machine renderer for local development, experimentation, and batch image
generation. Distribution is source-based (clone, build with the Gradle wrapper, run). Should
headless batch rendering at scale ever be needed, the CLI entry point and the `Context`
abstraction already make the core embeddable without the GUI.
