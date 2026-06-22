# Testing style & methodology

The single source of truth for how unit tests are written in this repo. The goal is one
consistent style across every contributor — human or agent — so tests read the same, fail
loudly, and stay trustworthy.

This guide **codifies the conventions already dominant in the suite** and names the few habits
to drop. When in doubt, copy the exemplars in [§13](#13-exemplars-copy-these).

---

## 1. Where tests live, what they run on

- Core, platform-independent tests: `src/commonTest/...` (mirrors `commonMain`).
- JVM-specific tests (renderers, JVM I/O): `src/jvmTest/...` (mirrors `jvmMain`).
- Test runner: **Kotest** on the JUnit 5 platform. Assertions: `kotest-assertions-core`.
- One test class per production class, named `FooTest` for `Foo`, in the same package.
- **Not unit-tested by design** (JaCoCo-excluded): `examples/**` (scenes), `MainKt`/CLI, and
  `ui/swing/**`. Verify changes there by rendering a scene or exercising the CLI, and say so —
  don't add unit tests purely to satisfy a rule. See CLAUDE.md → *Ways of working*.

## 2. Spec style: `StringSpec` only

Every test class is a `StringSpec`. Don't mix in `FunSpec`, `DescribeSpec`, `BehaviorSpec`.

```kotlin
class SphereTest : StringSpec({
    "ray through the centre hits the sphere" {
        // ...
    }
})
```

## 3. Matchers: Kotest, never JUnit

Use Kotest infix matchers (`shouldBe`, `shouldNotBe`, `shouldThrow`, `shouldBeInstanceOf`,
`shouldHaveSize`, …). No `assertEquals` / `assertTrue` / `org.junit.*` assertions — the suite
has zero and keeps it that way.

## 4. Comparing doubles: go through `shouldBeApprox`

Never compare a computed `Double` (or `Point3D`/vector built from doubles) with bare `shouldBe`
— floating-point error makes that flaky or false-green. Use the shared matchers in
`src/commonTest/.../Fixture.kt`:

```kotlin
sr.t shouldBeApprox 2.0                 // Double, tolerance = MathUtils.K_EPSILON
point shouldBeApprox Point3D(1.0, 0.0, 0.0)
```

- Default tolerance is `MathUtils.K_EPSILON`. That is the right default for geometry/intersection
  math.
- Need a *different* tolerance for one assertion? Use Kotest's `plusOrMinus` and put the value
  inline, with a reason if it isn't obvious: `result shouldBe (1.0 plusOrMinus 1e-3)`.
- Exact integer/enum/boolean/exact-by-construction values (e.g. a basis that comes out as exactly
  `(1.0, 0.0, 0.0)`) may use plain `shouldBe`.
- Missing a matcher for a type (e.g. a vector)? Add it to `Fixture.kt` rather than open-coding
  per-component comparisons in each test.

## 5. Naming: describe the behaviour, not the method

The description is a sentence about *what should happen*, not the method under test.

```kotlin
// good — scenario + expected outcome
"q=0 plane, point below, vector up, hit" { ... }
"parallel renderer fails on incompatible resolution" { ... }

// avoid — echoes the method/JUnit habit, says nothing about behaviour
"testComputeUVW" { ... }
"testSolveCubic2" { ... }
```

## 6. Structure: Arrange–Act–Assert, and every test must assert

- Lay tests out as arrange → act → assert, separated by **blank lines**. `// given/when/then`
  comments are optional — use them only when a step is non-obvious; don't sprinkle them on
  three-line tests. Pick one approach within a file.
- **Every test makes at least one meaningful assertion.** Hard rules:
  - **No `println` in place of an assertion.** A print-only test cannot fail and hides bugs —
    that is literally how the `solveQuartic` defect (TASK-23) went unnoticed.
  - **No tautological assertions.** `x shouldNotBe null` on a non-nullable type is always true
    and tests nothing. Assert the actual value/contract instead.
  - Assert the **outputs that matter**, not just one. `PlaneTest` checks the hit flag, the `t`
    distance, *and* the surface normal — not merely "it hit".

## 7. What to assert: behaviour through the public contract

- Prefer the **public contract** (return values, observable effects) over internal structure.
- **Avoid reflection into private fields** to inspect or mutate internals. It makes the test
  brittle to legitimate refactors — the opposite of what a refactor-guarding test should do
  (CLAUDE.md → *cover first*). If a behaviour genuinely needs internal tuning to test, prefer a
  real seam (constructor parameter / injected config) over reflection. Existing reflection-based
  tests (e.g. `GridStructuresTest`) are tolerated, not a model to copy.
- **Fakes over mock frameworks.** Hand-write a small fake implementing the interface and assert
  against what it recorded. `RendererTest` is the template: `RecordingFilm` captures pixel
  writes, `StubSingleRayRenderer` returns a fixed colour, `IdentityCorrector` is a no-op — then
  the test asserts "all pixels were written" and "bad resolution throws".

## 8. Reuse: fixtures, custom matchers, shared invariants

- **Constants**: shared material/colour constants live in `Fixture.Ex` (`ka`, `kd`, `cd`, …).
  Reuse them instead of inventing per-file magic values.
- **Custom matchers**: domain matchers belong in `Fixture.kt` (`shouldBeApprox`) or a local
  helper when scoped to one area (`Point2D.shouldBeWithinCube` in `samplers/Tests.kt`).
- **Shared invariants across implementations**: when several types must satisfy the same
  contract (all samplers: right count, inside the unit square, well-distributed), express the
  invariant once as a `stringSpec { }` factory and `include(...)` it from each test. See
  `samplers/Tests.kt` + `PureRandomTest`. This is the project's substitute for parameterised
  tests.
- **Test data builders**: in a *unit* test, construct objects with **raw constructors**
  (`Sphere(...)`, `Vector3D(...)`) for clarity and isolation. Reserve the **`Builder.build { }`
  DSL** for integration-level tests that exercise world assembly (`BuilderTest`, DSL scope
  tests).
- **Magic numbers**: if a test value is chosen for a reason (a prime to avoid coincidental
  cancellation, an edge offset like `0.1234`), a short inline comment on *why* saves the next
  reader. Don't comment self-evident values.

## 9. Determinism & isolation

Tests must be deterministic and order-independent. Watch for:

- **Time**: avoid asserting on wall-clock from `Thread.sleep` (see `TimerTest`, mildly flaky).
  If you must, give generous margins and note the risk.
- **Randomness**: the sampler/`Random` tests check *statistical* invariants over many draws
  (counts, histogram bucket coverage) rather than exact values — that's the right pattern for
  randomised code. Don't assert exact values from an unseeded RNG.
- **Files**: tests that read resources (e.g. `PlyReaderTest`) must resolve paths robustly, not
  assume a working directory. Prefer the test classpath/resources over relative paths.
- **Shared mutable / static state**: don't depend on test execution order. If a test must toggle
  global state, restore it in a `finally`.

## 10. Property-based testing (opt-in, for the math core)

The math is full of algebraic laws that example-based tests only spot-check. Property tests are
**encouraged for `math/`** (and similar pure code) but are **not yet wired up**: add
`testImplementation(Testing.kotest.property)` to `build.gradle.kts` first (it is not on the
classpath today).

Good candidates — laws that should hold for *all* inputs:

- `v + w == w + v`; `(a + b) + c == a + (b + c)`
- `v.normalize().length()` ≈ `1`
- `invMatrix * (forwardMatrix * p)` ≈ `p` (round-trip — `AffineTransformationTest` already
  asserts exactly this for hand-picked points; generalise it)
- every root returned by a polynomial solver satisfies the polynomial (this property is what
  would have caught TASK-23 immediately)

```kotlin
// after adding the kotest-property dependency
"vector addition is commutative" {
    checkAll(arbVector3D, arbVector3D) { v, w ->
        (v + w) shouldBeApprox (w + v)
    }
}
```

Keep it proportionate: property tests complement example tests for laws; they don't replace the
readable, scenario-named tests that document specific behaviours.

## 11. Characterization tests & the cover-first rule

Before refactoring production code, it must be covered by a test that pins current behaviour and
**does not change** across the refactor (CLAUDE.md → *Ways of working*). A characterization test
may legitimately pin behaviour that is *currently wrong* — when it does, **say so loudly**: a
comment explaining the expected-correct value and a reference to the tracking task, so the pin
reads as "known bug, frozen" and not "verified correct". Example: the `solveQuartic` case in
`PolynomialsTest` pinned to its wrong output with a `TASK-23` reference.

## 12. Anti-pattern checklist (reject in review)

- [ ] A test with no assertion / only `println`.
- [ ] A tautology (`shouldNotBe null` on a non-nullable; asserting a value against itself).
- [ ] Bare `shouldBe` on computed doubles.
- [ ] Asserting only one of several meaningful outputs.
- [ ] Reflection into private fields where a seam would do.
- [ ] A mock framework where a 10-line fake would do.
- [ ] Method-name test descriptions (`testFoo`).
- [ ] Pinned wrong values with no comment/task reference.
- [ ] Hidden non-determinism (time, unseeded RNG asserted exactly, working-dir file paths).

## 13. Exemplars (copy these)

- **Geometry / intersection**: `src/commonTest/.../objects/PlaneTest.kt` — scenario names, AAA,
  asserts hit flag + `t` + normal.
- **Collaborators / strategies with fakes**: `src/jvmTest/.../renderer/RendererTest.kt`.
- **Shared invariants via `include`**: `src/commonTest/.../samplers/Tests.kt` + `PureRandomTest.kt`.
- **Custom matchers / round-trip properties**: `src/commonTest/.../Fixture.kt`,
  `AffineTransformationTest.kt`.
