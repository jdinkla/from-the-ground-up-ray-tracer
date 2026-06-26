# 8. Crosscutting Concepts

Concepts and conventions that apply across many building blocks.

## 8.1 Domain model and math core

The domain is modelled with small, immutable value types in `math/` and `colors/`:
`Point3D`, `Vector3D`, `Normal`, `Matrix`, `BBox`, and `Color`. They overload operators
(`+`, `-`, `*`, `dot`) so geometric and colour arithmetic reads like the formulas in Suffern's
book. Geometry implements one interface, `IGeometricObject` (`hit`, `shadowHit`, `boundingBox`,
`normal`); materials and lights are likewise small interfaces. The `World`/`IWorld` aggregate is
the root that ties camera, view plane, lights, materials, and objects together.

## 8.2 Scene description DSL

Scenes are written declaratively with `Builder.build { … }`. Nested scope receivers
(`WorldScope`, `LightsScope`, `MaterialsScope`, `ObjectsScope`, `InstanceScope`,
`MetadataScope`) expose blocks like `camera(...)`, `ambientLight(...)`, `lights { }`,
`materials { }`, `objects { }`. Materials are declared once with a string `id` and referenced by
that id from objects. Helper functions (`p()`, `v()`, `n()`, `c()`) keep coordinates and colours
terse. The DSL separates *what a scene is* from *how it is rendered*.

## 8.3 Concurrency and parallelism

Parallelism is a first-class, *pluggable* concern, not baked into the renderer: the `IRenderer`
strategy decides how per-pixel work is distributed (sequential, fork/join, parallel streams,
coroutines, virtual threads — see [Chapter 6](06_runtime_view.md)). Correctness rests on
**immutability**: the scene is fully built and `initialize()`d before rendering starts, so
worker threads only read shared state and write disjoint pixels. The block-based renderers
partition the image into independent rectangles.

## 8.4 Configuration and discovery

Command-line configuration is parsed with **Clikt** (`CommandLine`). Resolutions are predefined
ids (`480p`…`4320p`) resolved by `Resolution.fromId`, which fails fast on an unknown id. Scenes
are discovered dynamically via **ClassGraph** rather than registered, so configuration of *which
scenes exist* is implicit in the source tree.

## 8.5 Numerical precision

Floating-point robustness is handled uniformly through `MathUtils.K_EPSILON`: intersection tests
offset by epsilon to avoid self-shadowing ("shadow acne") and to reject near-tangent roots. Tests
compare floats with approximate matchers (`shouldBeApprox`) rather than exact equality.

## 8.6 Logging

A lightweight `Logger` provides levelled logging (used for progress on long renders, e.g. PLY
parsing and grid construction). On the JVM it is backed by Logback. Logging is for developer
visibility, not structured/operational telemetry.

## 8.7 Error handling

The system favours **fail-fast** for configuration and **typed exceptions** for resource limits:
unknown world/resolution ids and malformed scene input raise descriptive errors immediately
(e.g. `PlyLimitExceededException` for an oversized PLY header). On the rendering hot path,
recoverable gaps (such as a missing material) degrade to a sentinel colour rather than aborting
the frame.

## 8.8 Testing approach

Tests use **Kotest** `StringSpec` on the JUnit 5 platform, with shared fixtures in
`commonTest/.../Fixture.kt`. The project's methodology is documented in `specs/testing.md` and is
binding for all contributors. Refactoring is **cover-first**: production code must be pinned by a
frozen characterization test before it is changed, and that test must not be edited to make the
refactor pass. Coverage-excluded zones (`examples/**`, the CLI entry point, the Swing UI) are
verified by rendering instead of unit tests.

## 8.9 Static analysis and code style

**Detekt** (config `detekt-config.yml`) enforces complexity, naming, and style rules across the
core and test source sets, and over the example scenes with a documented relaxed subset
(MagicNumber/LongMethod/MaxLineLength are unsuitable for declarative scene data). **JaCoCo**
tracks coverage with deliberate exclusions for the coverage-excluded zones. Both run as part of
the single `./gradlew check` gate, alongside official Kotlin code style.
