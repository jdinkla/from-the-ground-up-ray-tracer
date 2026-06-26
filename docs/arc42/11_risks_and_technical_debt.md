# 11. Risks and Technical Debt

This chapter records the **current** technical risks and accepted technical debt. The live,
authoritative backlog of tracked debt is the project's **Backlog** (`backlog/`), not this file;
this chapter is a snapshot and a map into it.

> **Note on history.** Earlier revisions of this chapter (and the now-removed `TECH_DEBT.md` /
> `TECH_DEBT_REPORT.md`) described a much older state — ~51 % coverage, "70+ `!!` operators",
> open Grid/SparseGrid duplication, and an "unpinned dependencies" risk. Backlog work
> (TASK-2/3/5, the coverage tasks, and TASK-57–64) has since paid that down; the figures below are
> measured against the current tree.

## 11.1 Current state

`./gradlew clean check` (compile + tests + Detekt) is **green**, and there are **no
production-risk (P0) items**. Measured against the current tree:

| Metric | Value | Source |
|--------|-------|--------|
| Instruction coverage | ~91 % | JaCoCo (`build/reports/jacoco/test/jacocoTestReport.xml`) |
| Branch coverage | ~89 % | JaCoCo |
| `!!` (not-null assertions) in production | **1** | `grep '!!' src/commonMain src/jvmMain` — the sole use is in the coverage-excluded Swing UI |
| Grid/SparseGrid `hit()` duplication | resolved | shared `GridTraversal.kt` (TASK-2/3) |
| `ParallelRenderer` error handling | resolved | records the first worker failure and rethrows with cause (TASK-5) |

Coverage and Detekt figures evolve with the code; treat the generated reports as authoritative
rather than any number transcribed here. The remaining debt is about **maintainability, drift, and
tooling trust**, not correctness.

## 11.2 Open technical risks

| Risk | Likelihood | Impact | Mitigation / status |
|------|-----------|--------|---------------------|
| **Detekt pinned to a 2.0 pre-release alpha** | Medium | Medium | `dev.detekt 2.0.0-alpha.x` is **required** — it is the only Detekt line supporting Kotlin 2.3 / JDK 25; 1.23.x stable does not. Documented inline in `build.gradle.kts`/`versions.properties`; the move to 2.0 stable is tracked as a Backlog task. |
| **Warnings are not yet errors** | Low | Low | The core compiles warning-clean (the unchecked casts were fixed in TASK-61), but `allWarningsAsErrors` is not yet enabled, so a new warning would not fail the build. Tracked as a Backlog follow-up. |
| **Memory-intensive scenes** | Medium | Medium | Dense grids and large PLY meshes (`Bunny4K.ply`, `Isis.ply`) are built fully in memory; the 8 GB heap in `gradle.properties` accommodates the bundled scenes. PLY headers are bounded by a sanity limit (`PlyLimitExceededException`) so a hostile/oversized header fails fast instead of exhausting the heap. |
| **No streaming for very large meshes** | Low | Medium | Mesh import loads the whole file; acceptable for the bundled teaching models, a constraint only for arbitrarily large external meshes. |

## 11.3 Dependency risks

Dependencies are managed with the **refreshVersions** Gradle plugin: `versions.properties` is the
single pinned source of versions (with `# available=` update hints), refreshed via
`./gradlew refreshVersions`. This is a deliberate, working mechanism — **not** an "unpinned
dependencies / missing version catalog" risk (a false positive in older revisions of this doc).

| Dependency | Risk | Note |
|------------|------|------|
| **Detekt (dev.detekt 2.0 alpha)** | Medium | Pre-release; see 11.2. Required by the Kotlin 2.3 / JDK 25 toolchain. |
| **KorIM (korim)** | Low | Image I/O from the Korlibs ecosystem; less mainstream than ImageIO but isolated behind the `films/` abstraction. |
| **ClassGraph** | Low | Classpath scan for scene discovery; keep compatible with the JDK in use. |
| **Clikt, Kotest, Logback, Kotlinx Coroutines** | Low | Mainstream, actively maintained. |

All dependencies use permissive licenses (Apache 2.0 / MIT); no restrictive-license risk identified.

## 11.4 Accepted / lower-priority debt

These are maintainability items, not defects. They are tracked (or closed) in the Backlog:

- **Long methods / complexity hotspots** in a few acceleration and math helpers
  (`GridUtilities`, `Polynomials`) remain longer than the Detekt threshold; they are localized and
  covered by tests.
- **KDoc coverage** of public APIs is improving incrementally as areas are touched, not yet complete.
- **Scene-DSL Detekt subset**: `examples/**` are linted with a deliberately relaxed rule subset
  (MagicNumber/LongMethod/MaxLineLength), since literal coordinates/colours and flat builder methods
  are the nature of declarative scene data (TASK-58).

## 11.5 Recently resolved (do not re-open)

So onboarding readers do not redo finished work, the following were paid down and are **closed**:

| Item | Resolution |
|------|------------|
| Coverage ~51 %/38 % | now ~91 %/89 % (coverage tasks) |
| 70+ `!!` operators | now 1 (in the coverage-excluded Swing UI) |
| Grid/SparseGrid `hit()` duplication & complexity | shared `GridTraversal.kt` (TASK-2/3) |
| `ParallelRenderer` swallowed threading errors | records & rethrows the first failure (TASK-5) |
| Detekt scanned non-existent dirs, skipped examples | source set fixed; examples linted (TASK-58) |
| "Pin Detekt to stable" recommendation | corrected: the 2.0 alpha is required for Kotlin 2.3/JDK 25 (TASK-59) |
| JDK version drift (21 vs 25) | aligned to 25 across build, CI, and docs (TASK-60) |
| Unchecked-cast warnings (`PlyReader`, `GridStructuresTest`) | made type-safe (TASK-61) |
| kd-tree "builder zoo" (6 builders) | consolidated to the 2 used (TASK-62) |
| Fat interfaces throwing `UnsupportedOperationException` (BRDF/BTDF/Light/Tracer) | segregated by role (TASK-63) |
| Dead deprecated `RayCast` tracer | removed (TASK-64) |

## 11.6 Where tracked debt lives

The Backlog (`backlog/`, via the `backlog` CLI) is the source of truth for committed/planned debt
work. This chapter and the Backlog should agree; when they drift, the Backlog wins. There is
deliberately **no** separate `TECH_DEBT.md` / tech-debt report document — they were consolidated
here to avoid a decorative, drifting second register.
