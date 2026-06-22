# 11. Risks and Technical Debt

This chapter documents technical risks and accumulated technical debt identified through static analysis, code review, and examination of the codebase structure.

## 11.1 Technical Risks

| Risk | Probability | Impact | Mitigation |
|------|-------------|--------|------------|
| **High cyclomatic complexity in core algorithms** | High | High | The `hit()` methods in `Grid.kt:164` (CC=33) and `SparseGrid.kt:108` (CC=36) far exceed the threshold of 15. These are critical path methods - bugs here affect all rendering. Refactor into smaller, testable units. |
| **Null safety violations** | High | Medium | 70+ uses of the `!!` operator across production code, particularly in `AreaLight.kt`, materials (`Phong.kt`, `Matte.kt`), and KDTree builders. Runtime NullPointerExceptions could crash rendering mid-process. Replace with safe calls or require non-null invariants. |
| **Low test coverage** | High | High | Current coverage: ~51% instructions, 38% branches. Critical acceleration structures (`Grid`, `SparseGrid`, KDTree builders) have 300-750+ missed instructions each. Increase coverage to 80%+ for core algorithms. |
| **Code duplication in acceleration structures** | High | Medium | `Grid.kt` (415 lines) and `SparseGrid.kt` (346 lines) share nearly identical `hit()` and `initialize()` implementations. Bug fixes must be applied twice. Extract common traversal logic into shared base class or strategy. |
| **Threading code error handling** | Medium | High | `ParallelRenderer.kt:31-37,78-84` catches `InterruptedException` and `BrokenBarrierException` but only prints stack trace - no recovery or propagation. Silent failures in parallel rendering are difficult to diagnose. |
| **Unpinned dependency versions** | Medium | Medium | Build uses `"_"` version placeholders without visible TOML catalog. Builds may break or behave differently across environments. Implement explicit version pinning via `libs.versions.toml`. |
| **Mutable global state** | Medium | Medium | `Grid.kt:410-414` exposes mutable companion object properties (`logInterval`, `factorSize`, `maxDepth`). Can cause race conditions in parallel rendering. Make immutable or pass as parameters. |
| **Memory-intensive operations without limits** | Medium | High | Grid acceleration structures allocate large arrays based on scene complexity with no bounds checking. Loading complex PLY models (e.g., `Bunny4K.ply`) could exhaust heap. Add configurable limits and graceful degradation. |

## 11.2 Technical Debt

| Debt Item | Location | Impact | Suggested Resolution |
|-----------|----------|--------|----------------------|
| **Long methods exceeding 60 lines** | `Grid.kt:164` (197 lines), `SparseGrid.kt:108` (193 lines), `Simple2Builder.kt:25` (129 lines), `ObjectMedianBuilder.kt:24` (103 lines), `GridUtilities.kt:13,133` (84, 96 lines), `Polynomials.kt:108` (74 lines) | High - reduces maintainability and testability | Extract cohesive blocks into private helper methods |
| **Suppressed code quality warnings** | `ObjectsScope.kt:29`, `WorldScope.kt:20`, `AffineTransformation.kt:8`, `Matrix.kt:11` (`TooManyFunctions`), `MaterialsScope.kt:26,44,74`, `World.kt:20`, `Transparent.kt:17` (`LongParameterList`) | Medium - indicates known violations | Audit necessity; refactor DSL scopes into smaller focused classes; use builder pattern for long parameter lists |
| **Deprecated code markers** | `World.kt:35,49` ("unused"), `KDTree.kt:38`, `SparseGrid.kt:336` ("shadowHit uses tmin as input?") | Low - code confusion | Remove unused code; clarify or fix shadowHit parameter semantics |
| **Deeply nested code blocks** | `Matte.kt:66` (`areaLightShade`), `Phong.kt:77` (`areaLightShade`), `OpenCylinder.kt:29` (`hit`) - 8 instances total exceeding nesting threshold of 4 | Medium - hard to reason about | Use early returns, extract nested blocks to methods |
| **Type checking anti-pattern** | `Grid.kt:341,360,378`, `SparseGrid.kt:139,158,177`, `Compound.kt:62,65` - repeated `is Compound` checks | Medium - violates open/closed principle | Introduce polymorphic `getResultObject()` method on `IGeometricObject` |
| **Missing documentation** | 4% comment ratio (484 comment lines / 10,794 SLOC); no KDoc on public APIs | Medium - steep learning curve | Add KDoc to public interfaces and complex algorithms (Polynomials, acceleration structures) |
| **Generic exception handling** | `Polynomials.kt:21` throws `AssertionError()` without message; `ParallelRenderer.kt:53` throws `RuntimeException` with cryptic message | Low - poor debuggability | Use specific exception types with descriptive messages |
| **Detekt maxIssues set to 150** | `detekt-config.yml:2` | Medium - allows accumulation of issues | Gradually reduce threshold as debt is paid down |

## 11.3 Dependency Risks

| Dependency | Risk | Details | Recommendation |
|------------|------|---------|----------------|
| **Version management** | Medium | Dependencies use `"_"` placeholder notation suggesting refreshVersions plugin, but no `versions.toml` found in repository root | Create explicit `gradle/libs.versions.toml` with pinned versions |
| **KorIM (korim)** | Low | Third-party image library from Soywiz/Korlibs ecosystem; less mainstream than alternatives | Monitor for breaking changes; consider fallback to standard ImageIO for JVM |
| **Logback Classic** | Low | Mature logging framework, well-maintained | Keep updated for security patches |
| **Clikt** | Low | CLI parsing library from AJ Alt; well-maintained | Current risk minimal |
| **ClassGraph** | Low | Reflection/classpath scanning; used for dynamic world discovery | Ensure version compatibility with Java 21 |
| **Kotest** | Low | Testing framework; actively maintained | Keep updated |
| **Kotlin Coroutines** | Low | Official JetBrains library | Low risk; follow Kotlin version compatibility |

**License Considerations**: All detected dependencies use permissive licenses (Apache 2.0, MIT). No restrictive license risks identified.

## 11.4 Operational Risks

| Risk | Current State | Impact | Recommendation |
|------|---------------|--------|----------------|
| **No structured logging** | Uses Logback but logging calls lack context (file, line, correlation IDs) | Medium - difficult to trace rendering issues | Add structured logging with MDC for render job correlation |
| **No health checks** | Application is a CLI/Swing GUI with no health endpoints | Low for desktop app | N/A for current use case; add if server deployment planned |
| **No performance metrics** | No instrumentation for render times, memory usage, or throughput | Medium - cannot identify bottlenecks | Add optional metrics collection for benchmarking |
| **No graceful shutdown** | Parallel renderers use `CyclicBarrier` but no shutdown hooks | Low | Add shutdown hooks to cleanly terminate thread pools |
| **Large file handling** | PLY model loading (`Bunny4K.ply`, `Isis.ply`) loads entire file into memory | Medium - OOM risk | Implement streaming parser for large models |
| **No input validation** | Command-line arguments parsed without bounds checking | Low | Add validation for resolution, world file existence |

## 11.5 Code Quality Metrics Summary

From Detekt static analysis:

| Metric | Value | Assessment |
|--------|-------|------------|
| Total code smells | 37 | Moderate |
| Code smells per 1,000 LOC | 4 | Acceptable |
| Cyclomatic complexity (total) | 1,347 | Elevated for ~13k LOC |
| Cognitive complexity | 1,058 | Moderate |
| Methods exceeding CC threshold (15) | 6 | Needs attention |
| Methods exceeding length threshold (60) | 10 | Needs attention |
| Nested blocks exceeding depth (4) | 8 | Needs attention |

## 11.6 Prioritized Remediation Roadmap

### Critical (Address First)
1. Refactor `Grid.hit()` and `SparseGrid.hit()` - complexity 33/36, 197/193 lines
2. Eliminate Grid/SparseGrid duplication via shared traversal strategy
3. Replace unsafe `!!` assertions with proper null handling or require-not-null contracts
4. Improve exception handling in `ParallelRenderer` threading code

### High Priority
5. Increase test coverage to 80%+ for acceleration structures
6. Extract long methods in KDTree builders (100+ lines)
7. Add explicit dependency version pinning
8. Refactor `GridUtilities` tessellation methods (84-96 lines)

### Medium Priority
9. Add KDoc documentation to public APIs
10. Address suppressed warnings systematically
11. Remove deprecated/unused code
12. Refactor nested code blocks in materials

### Low Priority
13. Improve error messages in exception handling
14. Consider extracting common type-checking patterns
15. Reduce Detekt `maxIssues` threshold incrementally