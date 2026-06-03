# 9. Architectural Decisions

Now I have all the information I need. Let me generate the arc42 Chapter 9: Architectural Decisions document.

# 9. Architectural Decisions

This chapter documents the key architectural decisions made in the ray tracer project using the Architecture Decision Record (ADR) format. These decisions were inferred from the codebase implementation and project history.

---

## ADR-1: Kotlin as Primary Language with JVM 21 Target

**Status:** Accepted

**Context:**
The ray tracer was originally implemented in Java with a Groovy DSL (pre-2018). As the codebase evolved, the need arose for better type safety, null safety, and a more expressive syntax for the scene description DSL. The project required modern concurrency features while maintaining access to the mature Java ecosystem for image processing and parallel execution.

**Decision:**
Migrate from Java/Groovy to Kotlin targeting JVM 21, using Kotlin 2.2.x. The project leverages:
- Kotlin's type-safe builder pattern for the DSL
- Null safety to prevent runtime NullPointerExceptions in complex ray-object intersections
- Extension functions and sealed classes for cleaner geometric object hierarchies
- Java 21's Virtual Threads (Project Loom) alongside Kotlin coroutines

**Consequences:**
- **Positive:** Compile-time null safety eliminates an entire class of runtime errors in geometric calculations. The DSL syntax (`Builder.build { camera(...) objects { sphere(...) } }`) is significantly more readable than the previous Groovy implementation.
- **Positive:** Access to Java 21 Virtual Threads enables lightweight concurrency without coroutine overhead for certain rendering strategies.
- **Negative:** Kotlin Multiplatform was attempted (2022-2023) but abandoned due to API instability. The project retains the `commonMain`/`jvmMain` structure for potential future multiplatform support.
- **Neutral:** Requires JDK 21+ runtime, limiting deployment to modern Java environments.

---

## ADR-2: Six-Strategy Concurrent Renderer Pattern

**Status:** Accepted

**Context:**
Ray tracing is an embarrassingly parallel problem—each pixel can be computed independently. However, the optimal parallelization strategy depends on scene complexity, hardware resources, and overhead characteristics. The project needed to support experimentation with different concurrency models while maintaining a clean abstraction for rendering.

**Decision:**
Implement six distinct renderer strategies via the Strategy pattern, selectable at runtime:

| Strategy | Implementation | Use Case |
|----------|---------------|----------|
| `SEQUENTIAL` | Single-threaded loop | Baseline/debugging |
| `FORK_JOIN` | Java ForkJoinPool with RecursiveAction | CPU-bound, work-stealing |
| `PARALLEL` | Fixed thread pool with CyclicBarrier | Traditional threading |
| `NAIVE_COROUTINE` | One coroutine per pixel | Coroutine overhead baseline |
| `COROUTINE` | Block-based coroutine batching | Optimal coroutine strategy |
| `VIRTUAL` | Java 21 Virtual Threads | Lightweight threading |

All renderers share a common block-partitioning strategy (`Block.kt`) dividing the image into a configurable grid (typically 32×32 blocks).

**Consequences:**
- **Positive:** Enables direct performance comparison between concurrency models. Production renders use `COROUTINE` or `VIRTUAL` for optimal throughput.
- **Positive:** Block-based partitioning improves cache locality and reduces false sharing between threads.
- **Negative:** Six implementations increase maintenance burden. Test coverage for threading paths is incomplete (documented in `TECH_DEBT.md`).
- **Neutral:** Runtime selection via CLI (`--renderer=COROUTINE`) allows users to experiment without recompilation.

---

## ADR-3: Type-Safe DSL for Scene Description

**Status:** Accepted

**Context:**
Describing complex 3D scenes requires specifying cameras, lights, materials, and geometric objects with their spatial relationships. The original Groovy DSL was dynamic and error-prone. Scene files needed to be both human-readable and compile-time verified.

**Decision:**
Implement a Kotlin type-safe builder DSL using scoped extension functions. The DSL architecture consists of:

- **`WorldScope`**: Top-level receiver with methods for `camera()`, `lights {}`, `materials {}`, `objects {}`
- **Nested scopes**: `MaterialsScope`, `LightsScope`, `ObjectsScope`, `InstanceScope`
- **Helper functions**: `p()` for Point3D, `c()` for Color, `n()` for Normal, `v()` for Vector3D
- **String-based material references**: Materials are defined with IDs and referenced by name in objects

Example scene definition:
```kotlin
Builder.build {
    camera(d = 1250.0, eye = p(0, 0.1, 10), lookAt = p(0, -1, 0))
    ambientLight(color = Color.WHITE, ls = 0.5)
    lights { pointLight(location = p(0, 5, 0), ls = 1.0) }
    materials { phong(id = "red", cd = c(1, 0, 0), ka = 0.5, kd = 0.8) }
    objects { sphere(center = p(0, 0, 0), radius = 1.0, material = "red") }
}
```

**Consequences:**
- **Positive:** Compile-time type checking catches errors like misspelled property names or incorrect parameter types immediately.
- **Positive:** IDE autocompletion and documentation work seamlessly within DSL blocks.
- **Negative:** String-based material IDs (`material = "red"`) require runtime lookup and can fail if the ID is misspelled. A sealed class approach would provide compile-time safety but reduce flexibility.
- **Neutral:** Scene files are Kotlin source files, requiring compilation. This trades runtime flexibility for type safety.

---

## ADR-4: Pluggable Acceleration Structures

**Status:** Accepted

**Context:**
Naive ray tracing tests every ray against every object (O(n) per ray). For complex scenes with thousands of objects, acceleration structures are essential. Different scene types benefit from different spatial data structures—uniform grids work well for evenly distributed objects, while KD-trees excel at scenes with varying object density.

**Decision:**
Implement acceleration structures as a pluggable hierarchy under `objects/acceleration/`:

- **`Grid`**: Uniform spatial subdivision with configurable cell multiplier (default 2.0×)
- **`SparseGrid`**: Memory-efficient variant using sparse cell storage
- **`KDTree`**: Spatial partitioning tree with multiple builder strategies:
  - `SpatialMedianBuilder` (default): Splits at spatial midpoint
  - `ObjectMedianBuilder`/`ObjectMedian2Builder`: Splits at object count midpoint
  - `TestBuilder`/`Test2Builder`/`Simple2Builder`: Experimental variants

Acceleration structures are selected at the DSL level:
```kotlin
objects {
    grid { /* objects added to grid */ }
    kdtree(builder = SpatialMedianBuilder()) { /* objects */ }
}
```

**Consequences:**
- **Positive:** Scenes can use the most appropriate acceleration structure. Complex mesh scenes typically use `kdtree`, while uniform distributions use `grid`.
- **Positive:** Multiple KD-tree builders enable experimentation with partitioning heuristics.
- **Negative:** Acceleration is chosen at scene definition time, not adaptively. A hybrid approach could auto-select based on object distribution.
- **Negative:** KD-tree builder implementations have low test coverage (documented in `TECH_DEBT.md`: 400-750 uncovered instructions each).

---

## ADR-5: Dynamic World Discovery via Classpath Scanning

**Status:** Accepted

**Context:**
The project contains 60+ example scenes (World files) in `src/examples/kotlin/`. Users need to select scenes at runtime without hardcoding a registry. Adding new scenes should not require modifying central configuration files.

**Decision:**
Use ClassGraph library to scan the classpath for classes implementing `WorldDefinition` interface:

```kotlin
// Worlds.kt
private val worldMap: Map<String, WorldDefinition> by lazy {
    ClassGraph()
        .enableClassInfo()
        .scan()
        .getClassesImplementing(WorldDefinition::class.java)
        .loadClasses(WorldDefinition::class.java)
        .associateBy { it.simpleName }
}
```

Worlds are then selectable via CLI: `--world=World66.kt`

**Consequences:**
- **Positive:** Adding a new scene requires only creating a new class implementing `WorldDefinition`. No registration needed.
- **Positive:** The Swing GUI dynamically populates its scene list from discovered worlds.
- **Negative:** Classpath scanning adds startup overhead (~100-500ms depending on classpath size).
- **Negative:** Requires all world classes to be on the classpath at runtime. Dynamic loading from external directories would require additional classloader configuration.
- **Neutral:** Lazy initialization defers scanning cost until first world access.

---

## ADR-6: Multiplatform-Ready Source Structure

**Status:** Accepted (with caveats)

**Context:**
In 2022-2023, the project experimented with Kotlin Multiplatform to enable running on JavaScript (browser) and Native targets. The APIs proved unstable, and the effort was abandoned. However, the source structure was retained for potential future reactivation.

**Decision:**
Maintain Gradle multiplatform source set structure:

```
src/
  commonMain/kotlin/    # Platform-independent code (~95% of codebase)
  commonTest/kotlin/    # Platform-independent tests
  jvmMain/kotlin/       # JVM-specific: renderers, Swing UI, Film impl
  jvmTest/kotlin/       # JVM-specific tests
```

Platform dependencies:
- `korim` (korlibs): Multiplatform image I/O library
- `kotlinx-coroutines`: Available on all Kotlin platforms
- Platform-specific code isolated to `jvmMain` (Virtual Threads, Swing, ForkJoin)

**Consequences:**
- **Positive:** Core ray tracing logic is platform-agnostic and could target Kotlin/JS or Kotlin/Native with minimal changes.
- **Positive:** Clear separation enforces thinking about what code genuinely requires JVM features.
- **Negative:** Currently unused complexity. The `jvmMain` module is the only active target.
- **Neutral:** korim dependency adds multiplatform image support even though only JVM PNG output is currently used.

---

## ADR-7: Kotest with JUnit 5 for Testing Strategy

**Status:** Accepted

**Context:**
The project needed a testing framework that supports:
- Descriptive test naming (specification-style)
- Property-based testing for mathematical operations
- Integration with Gradle and CI
- Good IDE support for test navigation

**Decision:**
Adopt Kotest 6.0.x with JUnit 5 runner:

- **Test style**: StringSpec for descriptive test names
- **Assertions**: Kotest matchers (`shouldBe`, `shouldContainExactly`, `shouldBeInstanceOf`)
- **Coverage**: JaCoCo with HTML/XML reporting
- **Exclusions**: UI code, examples, and entry points excluded from coverage metrics

Example test structure:
```kotlin
class SphereTest : StringSpec({
    "hit should return true for ray intersecting sphere" {
        val sphere = Sphere(Point3D.ORIGIN, 1.0)
        val ray = Ray(Point3D(0, 0, -5), Vector3D.FORWARD)
        sphere.hit(ray, hit) shouldBe true
    }
})
```

**Consequences:**
- **Positive:** Descriptive test names document expected behavior. StringSpec syntax is concise.
- **Positive:** Kotest matchers provide clear failure messages with expected vs actual values.
- **Negative:** Current coverage is ~51% instructions, 38% branches. Significant gaps exist in acceleration structures and renderer threading paths.
- **Neutral:** Migration from JUnit 4 to Kotest required rewriting test syntax but improved readability.

---

## Summary of Decision Drivers

| Decision | Primary Driver | Key Trade-off |
|----------|---------------|---------------|
| Kotlin + JVM 21 | Type safety, modern concurrency | No multiplatform deployment |
| Six Renderer Strategies | Performance experimentation | Maintenance complexity |
| Type-Safe DSL | Compile-time error detection | String-based material IDs |
| Pluggable Acceleration | Scene-appropriate optimization | Manual selection required |
| Dynamic World Discovery | Zero-config extensibility | Startup scanning overhead |
| Multiplatform Structure | Future portability | Currently unused |
| Kotest Testing | Readable specifications | Coverage gaps remain |