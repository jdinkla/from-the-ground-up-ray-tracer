# 4. Solution Strategy

This chapter summarises the fundamental decisions that shape the architecture. The detailed
rationale for individual technology choices lives in
[Chapter 9, Architectural Decisions](09_architectural_decisions.md); the constraints that
bound them are in [Chapter 2](02_architecture_constraints.md).

## 4.1 Technology decisions (summary)

| Decision | Choice | Why |
|----------|--------|-----|
| Language / platform | Kotlin on JVM 25 | Type safety, null safety, expressive DSLs; mature JVM ecosystem for image I/O and concurrency (see [ADR-1](09_architectural_decisions.md)) |
| Build & dependency management | Gradle (Kotlin DSL) + refreshVersions | Reproducible builds; `versions.properties` is the single place dependency versions are pinned |
| Concurrency | Kotlin coroutines **and** JVM threads / virtual threads | Lets the project compare parallelization strategies head-to-head |
| Image output | Korim (`films/`) | Cross-platform raster output behind a thin `Film` abstraction |
| Scene discovery | ClassGraph | Scenes register themselves; no central list to maintain |
| Quality gates | Detekt + JaCoCo + Kotest | Static analysis, coverage, and tests run as one `check` |

## 4.2 Top-level decomposition

The system is a **domain-aligned monolith**: packages are named after ray tracing concepts
(`objects/`, `materials/`, `lights/`, `cameras/`, `tracers/`, `samplers/`, `math/`) rather
than technical layers. Four patterns carry most of the structural weight:

- **Strategy** — pluggable algorithms behind small interfaces: `Tracer` (colour of a ray),
  `IRenderer` (how pixel work is parallelized), `ILens` (camera projection), sampler
  `IGenerator` (anti-aliasing pattern), and `TreeBuilder` (kd-tree split strategy). Each has an
  enum (`Tracers`, `Renderer`, …) mapping a CLI name to a constructor.
- **Composite** — `Compound` aggregates many `IGeometricObject`s and is itself an
  `IGeometricObject`; acceleration structures (Grid, KDTree) are compounds that intersect their
  children efficiently.
- **Builder (type-safe DSL)** — scenes are written with `Builder.build { … }`; nested scope
  receivers (`WorldScope`, `MaterialsScope`, `ObjectsScope`, …) give a declarative, validated
  scene language.
- **Source-set separation** — a platform-independent core (`commonMain`) is kept free of
  JVM-specific I/O, UI, and parallel renderers (`jvmMain`).

```
            +-------------------------------------------------+
  CLI/GUI ->|  Context (tracer + renderer + resolution)       |
            |                       |                          |
            |                       v                          |
  scene  -->|  WorldDefinition -> World -> initialize()        |
            |                       |                          |
            |                       v                          |
            |  IRenderer -> SingleRayRenderer(lens + Tracer)   |
            |                       |                          |
            |                       v                          |
            |  Film -> PNG                                     |
            +-------------------------------------------------+
```

## 4.3 How quality goals are achieved

| Quality goal (Ch. 1) | Solution approach |
|----------------------|-------------------|
| Performance efficiency | Six parallel `IRenderer` strategies; spatial acceleration (Grid, SparseGrid, KDTree); block-partitioned pixel work; generous JVM heap |
| Modifiability | Strategy interfaces + self-registering scenes; composition (materials compose BRDF/BTDF); DSL isolates scene description from rendering |
| Functional correctness | Dedicated value types (`Point3D`, `Vector3D`, `Normal`, `Color`); `K_EPSILON` intersection tolerances; explicit polynomial solvers; characterization tests |
| Usability | Declarative DSL with helper functions (`p()`, `v()`, `n()`, `c()`); auto-discovered scenes; CLI and GUI front ends |
| Maintainability | Detekt + JaCoCo + Kotest as a single gate; cover-first refactoring rule; domain-aligned packages |

## 4.4 Organizational decisions

- **CI** runs `./gradlew build` (compile + test + Detekt) on every push and PR to `main`
  (GitHub Actions, JDK 25 Temurin) — see [Chapter 7](07_deployment_view.md).
- **Cover-first refactoring**: production code must be covered by a frozen characterization
  test before it is refactored (the testable core only; `examples/**`, the CLI entry point, and
  the Swing UI are coverage-excluded and verified by rendering instead).
- **Scenes are data, not registrations**: adding a scene means adding one `WorldDefinition`
  object; ClassGraph discovers it at startup.
