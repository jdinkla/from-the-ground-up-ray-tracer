# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## What this is

A ray tracer in Kotlin, a port of the C++ code from Kevin Suffern's *Ray Tracing from
the Ground Up* (A K Peters, 2007), refactored toward an object-functional style. JVM-only, JDK 21.

**Lineage.** Suffern's C++ → a **Java** port (extended with concurrency/parallelism — the source
of the several parallel renderers) → a **Kotlin** port done in **2018–2020**, which is this
codebase. The old Java/Groovy version lives on the `groovy-java` branch. The **kd-tree**
acceleration structure is **not from the book**: it originates in Jörn's diploma thesis (originally
Haskell) and was ported in. The book's own acceleration structure is the uniform/regular grid.

## Project goals

1. **Stay modern Kotlin.** Keep the code idiomatic and current as Kotlin and the JDK evolve.
2. **Reach the book's feature set.** Extend the ray tracer where it still lags Suffern's *Ray
   Tracing from the Ground Up* — e.g. **textures are not yet implemented**. Work that fills a
   book-coverage gap is in-scope by default.

## Commands

```bash
./gradlew build          # compile + test + detekt (the full check)
just test                # = ./gradlew clean check
./gradlew test           # unit tests only (Kotest on the JUnit 5 platform)
./gradlew test --tests "net.dinkla.raytracer.materials.MatteTest"   # a single test class
./gradlew detekt         # static analysis (config: detekt-config.yml)
./gradlew jacocoTestReport   # coverage (runs after test; HTML in build/reports/jacoco)
./gradlew refreshVersions    # bump dependency versions (see versions.properties)
```

Running a render:

```bash
./gradlew swing          # interactive Swing GUI (pick a scene, render it)
./gradlew run --args="--world=World20.kt --tracer=AREA --renderer=FORK_JOIN --resolution=1080p"
```

CLI options (Clikt): `--world` (a scene id, default `World20.kt`), `--tracer`
(`WHITTED`/`AREA`/`MULTIPLE_OBJECTS`), `--renderer`
(`SEQUENTIAL`/`FORK_JOIN`/`PARALLEL`/`NAIVE_COROUTINE`/`COROUTINE`/`VIRTUAL`),
`--resolution` (`720p`/`1080p`/`2160p`/…).

## Source layout — note

The `src` tree uses Kotlin-Multiplatform-style source sets — `commonMain`, `jvmMain`,
`commonTest`, `jvmTest`, plus a separate `examples` set — but this is a **JVM-only project**
(`kotlin("jvm")`). Those directories are a vestige of an abandoned multiplatform experiment
and are wired up manually in `build.gradle.kts` (`sourceSets[...].kotlin.srcDir(...)`). Treat
`commonMain` as the platform-independent rendering core and `jvmMain` as JVM-specific I/O,
the Swing UI, and the parallel renderers. (`gemini.md` calls it a "multi-platform project" —
that description is now outdated.)

## Architecture

**Render pipeline.** `Main` / `CommandLine` parse the CLI into a
`Context(tracerCreator, rendererCreator, resolution)`. `Render.render` then: looks up a
`WorldDefinition` by id → calls `world()` to build the `World` → `context.adapt(world)` wires
the chosen tracer, a `SimpleSingleRayRenderer` (camera lens + tracer), and the `ViewPlane`
color corrector into the world → `world.initialize()` → `renderer.render(film)` → `film.save(png)`.

**Scenes are auto-discovered, not registered.** Each scene is a Kotlin `object` implementing
`WorldDefinition` (`id` + `world(): World`) under `src/examples/.../examples`. At startup
`Worlds.kt` uses **classgraph** to scan that package and build `worldMap` keyed by each
object's `id` (conventionally the file name, e.g. `"World48.kt"`). **To add a scene, just
create the object** — it registers itself; no list to edit.

**Scene DSL.** Scenes are written with `Builder.build { ... }`. The receiver scopes live in
`world/dsl/` (`WorldScope`, `LightsScope`, `MaterialsScope`, `ObjectsScope`, `InstanceScope`,
`MetadataScope`) and expose blocks like `camera(...)`, `ambientLight(...)`, `lights { }`,
`materials { }`, `objects { }`. Materials are declared with a string `id` and referenced by
that id from objects. See `README.md` for a full example.

**Core domain (`commonMain`).**
- `world/` — `World`/`IWorld` (camera, view plane, lights, materials map, objects, compound),
  `Context`, `Render`, the DSL.
- `tracers/` — strategies for the colour of a ray (`Whitted`, `AreaLighting`,
  `MultipleObjects`, …); the `Tracers` enum maps CLI names to constructors.
- `objects/` — geometric primitives (`Sphere`, `Plane`, `Triangle`, `Torus`, `Disk`,
  `OpenCylinder`, `Instance`, …) plus `acceleration/` (uniform grid, kd-tree), `mesh/`,
  `compound/`, `beveled/`, `arealights/`.
- `materials/`, `brdf/` (reflectance), `btdf/` (transmittance), `lights/`, `cameras/` (+ `lenses/`),
  `samplers/` (anti-aliasing), `colors/`, `math/`, `hits/`, `films/`.

**Parallel renderers (`jvmMain/renderer`).** The `Renderer` enum maps CLI names to
implementations that differ only in *how pixel work is parallelized* — `SequentialRenderer`,
`ForkJoinRenderer`, `ParallelRenderer`, coroutine variants, and `VirtualThreadBlockRenderer`
(block-based). They all drive the same single-ray renderer.

## Conventions

- **Use the Kotlin LSP for fast code lookups.** Prefer it for navigating symbols, finding
  definitions/references, and type info over text search (grep) — it understands the code and
  is faster and more accurate.
- **Testing style & methodology: `specs/testing.md` is the source of truth — read it before
  writing or reviewing tests.** It is mandatory for all contributors (human and agent) and
  codifies the rules summarised below.
- Tests use **Kotest** `StringSpec` (`"description" { ... }`); shared helpers/fixtures in
  `src/commonTest/.../Fixture.kt` (e.g. the `shouldBeApprox` infix matchers for float
  comparisons against `MathUtils.K_EPSILON`).
- Some mesh examples need `.ply` files; a few are bundled in `resources/`, others must be
  downloaded (see `README.md`).

## Ways of working

**Refactoring is guarded by a test that does not change.** Before refactoring any code, that
code must be covered by a unit test.

1. **Cover first.** If no test exercises the code you intend to refactor, write one *before*
   touching the production code, and confirm it passes against the current (unrefactored)
   behavior. This is a characterization test: it pins the existing behavior.
2. **Refactor.** Change the implementation only — behavior must stay the same.
3. **Green and frozen.** After the refactoring the test must pass, and it **must not be
   modified** to make it pass. If you find you need to change the test, the change is no longer
   a pure refactor (behavior changed) — stop and treat it as a behavior change, not a refactor.

**Exception — the coverage-excluded zones.** The "cover first" rule does **not** apply to the
code JaCoCo excludes: `examples/**` (scene definitions), `MainKt` / the CLI entry point, and the
Swing UI (`ui/swing/**`). These are not unit-tested by design (see Conventions). When a refactor
lands there, **verify it manually instead** — render a scene (`just run …` / `just swing`) or
exercise the CLI and confirm the output is unchanged — and say so. Don't add a unit test purely
to satisfy the rule in these areas. If a refactor straddles the boundary (e.g. wiring file-based
scene loading through `Main`/CLI per TASK-17), cover the part that lives in the testable core
(`commonMain`/`jvmMain` logic) with a frozen test, and manually verify the excluded glue.

<!-- BACKLOG.MD GUIDELINES START -->
<CRITICAL_INSTRUCTION>

## Backlog.md Workflow

This project uses Backlog.md for task and project management.

**For every user request in this project, run `backlog instructions overview` before answering or taking action.**

Use the overview to decide whether to search, read, create, or update Backlog tasks.

Use the detailed guides when needed:
- `backlog instructions task-creation` for creating or splitting tasks
- `backlog instructions task-execution` for planning and implementation workflow
- `backlog instructions task-finalization` for completion and handoff

Use `backlog <command> --help` before running unfamiliar commands. Help shows options, fields, and examples.

Do not edit Backlog task, draft, document, decision, or milestone markdown files directly. Use the `backlog` CLI so metadata, relationships, and history stay consistent.

</CRITICAL_INSTRUCTION>
<!-- BACKLOG.MD GUIDELINES END -->
