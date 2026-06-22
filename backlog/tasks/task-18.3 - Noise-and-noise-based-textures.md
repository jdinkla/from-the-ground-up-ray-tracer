---
id: TASK-18.3
title: Noise and noise-based textures
status: Done
assignee:
  - '@claude'
created_date: '2026-06-22 09:41'
updated_date: '2026-06-22 17:03'
labels:
  - enhancement
  - book-parity
dependencies:
  - TASK-18.1
parent_task_id: TASK-18
priority: low
ordinal: 21000
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Lattice noise and the noise-driven textures from the book (noise/ is currently empty). Implement a LatticeNoise base with value and gradient noise, LinearNoise and CubicNoise interpolation, plus fractal sum (fBm) and turbulence. Then the noise textures: FBmTexture, TurbulenceTexture, WrappedFBmTexture, RampFBmTexture (marble look), and a wood texture. Built on the Texture abstraction from the infra subtask.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 A LatticeNoise implementation provides value/gradient noise plus fBm and turbulence
- [x] #2 fBm, turbulence, and a marble-style (ramp-fbm) texture render via an SV material
- [x] #3 Noise textures are declarable from the Builder DSL
- [x] #4 Unit tests cover noise determinism/range and the fBm/turbulence helpers
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Study 18.1/18.2 texture layer + detekt MagicNumber config (ignorePropertyDeclaration/ignoreConstantDeclaration on -> table consts/local vars are clean). DONE.
2. noise/LatticeNoise.kt (abstract, commonMain): fixed Perlin 256-entry permutation table (PERMUTATION_SIZE=256); DETERMINISTIC value table (seeded LCG, values in [-1,1]) and gradient table (seeded LCG -> unit vectors). Abstract valueNoise(p)/vectorNoise(p) (interpolation in subclass) wrapping the lattice index helper INDEX(ix,iy,iz). Concrete fbm/turbulence/fractalSum on top, with configurable numOctaves/lacunarity/gain. No unseeded Random.
3. noise/LinearNoise.kt: trilinear interpolation of the 8 corner lattice values/gradients.
4. noise/CubicNoise.kt: tricubic (Hermite/4-point) interpolation.
5. textures/FBmTexture, TurbulenceTexture, WrappedFBmTexture, RampFBmTexture (reuse 18.2 Ramp via colorAt), Wood. Each samples noise at sr.localHitPoint, lerps min/max colour (or ramp). data classes, Texture impls, pure colourAt seam where useful.
6. AC#3: confirm svMatte/svPhong/svEmissive already take Texture -> no DSL change; declare textures in example scenes.
7. commonTest noise tests: determinism (same point -> identical value, both LinearNoise & CubicNoise), range (value noise in [-1,1] over many sampled points), fbm bounds & turbulence>=0 over many points, more octaves add detail. Texture tests: FBm/Turbulence/RampFBm/Wood colour within min..max, determinism via testShade. Derive expectations from math.
8. examples/textures/noise/: marble (RampFBm) sphere + turbulence + wood scene(s) via svMatte. Render WHITTED/SEQUENTIAL/720p, verify visually.
9. just test green incl detekt; keep noise detekt-clean via consts/locals; report.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
Added noise/ package (commonMain): SeededRandom (deterministic LCG, no kotlin.random), LatticeNoise (abstract; fixed 256-entry Perlin permutation table; deterministic value table [-1,1] and unit-vector gradient table from seed 253; index() lattice hash; fractalSum/fbm, turbulence, configurable octaves/lacunarity/gain), LinearNoise (trilinear), CubicNoise (tricubic four-knot spline). Added textures (commonMain): FBmTexture, TurbulenceTexture, WrappedFBmTexture, RampFBmTexture (marble, reuses 18.2 Ramp.colorAt), Wood (concentric rings + turbulence warp). Each samples sr.localHitPoint; each has a pure colorFor/colorAt seam. All compile.

VERIFICATION & HANDOFF.

AC#3 (DSL): confirmed NO DSL change needed. MaterialsScope.svMatte/svPhong/svEmissive already take the Texture interface, and all five noise textures (FBmTexture, TurbulenceTexture, WrappedFBmTexture, RampFBmTexture, Wood) implement Texture, so they are declarable from the Builder DSL as-is. Example scenes declare them via svMatte. Additive only: no existing textures/materials/Texture/DSL modified.

DESIGN CHOICES:
- Permutation table: the classic fixed 256-entry Perlin table (a constant in LatticeNoise.PERMUTATION), so the lattice hash is identical across runs/platforms.
- Value+gradient tables: built once from a fixed seed (DEFAULT_SEED=253) via SeededRandom, a self-contained Numerical-Recipes LCG -- NOT kotlin.random.Random -- so renders are reproducible. valueTable in [-1,1]; gradientTable unit vectors (rejection-sampled then normalised).
- CubicNoise scalar field is clamped to [-1,1] (cubic spline can overshoot); vector field is not clamped, so the gradient-component-in-[-1,1] test is asserted only on LinearNoise (trilinear = convex combination, provably bounded).
- fractalSum/turbulence normalise by the running max-amplitude sum, so output range is octave-count-independent: fbm in [-1,1], turbulence in [0,1]. fbm() is an alias for fractalSum().
- RampFBmTexture reuses 18.2 Ramp.colorAt, indexing it by (1+sin(axis*freq + amp*fbm))/2 (marble).

UNIT TESTS (commonTest, derived from the math, not round-tripped):
- LatticeNoiseTest: determinism (same point/instance/seed -> identical, exact shouldBe), value-noise range [-1,1] over 7 sample points (linear+cubic), lattice-point=corner-value (and two lattice points differ -> field varies), single-octave fbm == valueNoise, single-octave turbulence == |valueNoise|, fbm in [-1,1] and turbulence in [0,1]/>=0 over octave counts {1,2,4,8}, more-octaves-add-detail, linear gradient components in [-1,1] (convex combination), gradient determinism.
- SeededRandomTest: same-seed reproducibility (unit + vector sequences), ranges [0,1)/[-1,1), nextUnitVector unit-length.
- NoiseTexturesTest: pure colorFor/colorAt seams with hand-derived expectations (fbm 0->mid, +/-1->max/min, windowed; turbulence 0/0.5/1; wrapped 0.25->0.5, 0.6->0.2; marble sin index; wood ring edge=light / centre=dark) + getColor determinism via testShade.

MANUAL VERIFICATION (examples are coverage-excluded):
- ./gradlew run --world=MarbleScene.kt --tracer=WHITTED --renderer=SEQUENTIAL --resolution=720p -> cream sphere with irregular dark-blue marble veins that bend/fold; correct.
- ./gradlew run --world=NoiseTexturesScene.kt ... -> three spheres: cloud-like blue fBm | billowy white/purple turbulence | brown warped concentric wood rings; all distinct and correct.
Both scenes auto-registered via classgraph (rendered by id without any registration edit).

just test (= ./gradlew clean check) BUILD SUCCESSFUL incl detekt (clean; moved Wood default colours to companion vals to satisfy MagicNumber on default-parameter literals; no baseline entries). Pre-existing unrelated Unchecked-cast warnings remain in PlyReader.kt/GridStructuresTest.kt.
<!-- SECTION:NOTES:END -->

## Final Summary

<!-- SECTION:FINAL_SUMMARY:BEGIN -->
Implemented the book's lattice-noise system and noise-driven textures (additive only — 14 new files, zero tracked-file modifications). New noise/ package: LatticeNoise (abstract base, value + gradient noise, fractalSum/fbm, turbulence) with LinearNoise (trilinear) and CubicNoise (tricubic four-knot spline) interpolation variants. Determinism (AC#4 headline) guaranteed by a self-contained seeded LCG (SeededRandom, Numerical-Recipes constants) + a fixed 256-entry Perlin permutation table — NO kotlin.random.Random/Math.random()/time anywhere, so renders are reproducible (reviewer verified). Five noise textures sample noise at sr.localHitPoint: FBmTexture, TurbulenceTexture, WrappedFBmTexture, RampFBmTexture (marble — ramp indexed by (1+sin)/2 of an fbm-warped value, reusing 18.2's Ramp), Wood. AC1-4 all met. AC#3 needed no DSL change (svMatte/svPhong/svEmissive already accept Texture; scenes declare via svMatte). Cover-first: SeededRandomTest (same-seed reproducibility, ranges, unit-vector length), LatticeNoiseTest (determinism, [-1,1] range, lattice-point=corner, single-octave fbm=valueNoise, single-octave turbulence=|valueNoise|, turbulence in [0,1] across octave counts, gradient convex-bound), NoiseTexturesTest (hand-derived colorFor + getColor determinism) — reviewer confirmed hand-derived, not round-trips; CubicNoise.valueNoise clamps the overshoot-prone scalar while vectorNoise is intentionally unclamped (gradient bound asserted only on the provably-bounded LinearNoise). Reviewer independently verified every texture's blend factor stays in [0,1] so output colors are valid. Example scenes MarbleScene + NoiseTexturesScene rendered + visually verified by both implementer and reviewer (cream/dark-blue folded marble veins; distinct fBm/turbulence/wood spheres; 123KB/151KB PNGs, no NaN/crash). detekt clean, no baseline entries. Verified via just test (clean check + detekt + jacoco) BUILD SUCCESSFUL. Committed as b2115f0. Completes the TASK-18 texture umbrella (18.1+18.2+18.3 all Done). Minor NIT (non-blocking): one tautological assertion line (abs(...)>=0.0) in LatticeNoiseTest — harmless, surrounding asserts are meaningful.
<!-- SECTION:FINAL_SUMMARY:END -->
