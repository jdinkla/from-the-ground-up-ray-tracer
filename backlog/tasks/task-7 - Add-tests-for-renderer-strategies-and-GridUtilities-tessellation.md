---
id: TASK-7
title: Add tests for renderer strategies and GridUtilities tessellation
status: Done
assignee:
  - '@claude'
created_date: '2026-06-22 09:11'
updated_date: '2026-06-22 11:44'
labels:
  - testing
  - concurrency
dependencies: []
references:
  - docs/arc42/11_risks_and_technical_debt.md
  - TECH_DEBT.md
priority: high
ordinal: 7000
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
GridUtilities.kt is the single largest coverage gap (1282 missed instructions) - tessellation helpers for flat/smooth spheres are untested (loops, pole triangles, normals). Renderer threading paths are also uncovered: ForkJoinRenderer, CoroutineBlockRenderer, NaiveCoroutineRenderer, and VirtualThreadBlockRenderer need success and barrier/failure tests mirroring the existing ParallelRenderer tests.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 GridUtilities tessellation covered for flat and smooth spheres across varying step counts (loops, pole triangles, normals)
- [x] #2 ForkJoin, Coroutine, NaiveCoroutine, and VirtualThread renderers have success and failure/barrier tests
- [x] #3 All renderer strategies produce equivalent output for a small reference scene
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Read GridUtilities.kt + renderer impls + existing RendererTest; capture baseline jacoco coverage.
2. AC#1: GridUtilitiesTest (commonTest) - flat+smooth tessellation across step counts: triangle counts 2*h*(v-1), pole-cap vertices, unit-sphere vertices, smooth radial per-vertex normals vs flat single face normal. Add Normal.shouldBeApprox to Fixture.kt.
3. AC#2: extend RendererTest with success + failure tests for ForkJoin/Coroutine/NaiveCoroutine/VirtualThread. Block renderers degrade silently (no throw) on sub-block resolutions -> pin observable shortfall, not a typed exception (verified via probe).
4. AC#3: equivalence test rendering a 32x32 positional reference through Sequential/ForkJoin/Parallel/NaiveCoroutine/Coroutine/VirtualThread; assert pixel-exact equality.
5. just test green; record before/after coverage.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
Additive testing task; no production code changed.

Files:
- src/commonTest/.../utilities/GridUtilitiesTest.kt (new) - 9 tests for tessellateFlatSphere/tessellateSmoothSphere.
- src/commonTest/.../Fixture.kt - added Normal.shouldBeApprox matcher.
- src/jvmTest/.../renderer/RendererTest.kt - extended from 2 to 12 tests.

AC#1 (GridUtilities): covered flat+smooth across step pairs (3,3)(4,4)(5,3)(6,5). Assert triangle count = 2*h*(v-1) which exercises top cap + bottom cap + middle-ring loop; pole-triangle vertices (v0=north pole for top cap, v1=south pole for bottom cap); every vertex on the unit sphere; smooth assigns radial per-vertex normals (n_i == normalize(v_i)) incl. north-pole = Normal.UP; flat exposes a single unit face normal (no per-vertex seams). shouldBeApprox used for all doubles/points/normals.

AC#2 (renderer success/failure): ForkJoin/Coroutine/NaiveCoroutine/VirtualThread each have a success test (block-aligned resolution fills every pixel) and a deterministic failure test. IMPORTANT per TASK-5 guidance - asymmetric guard behavior, verified empirically via a throwaway probe: only ParallelRenderer throws IllegalArgumentException (height not divisible by numThreads/4). The block renderers (ForkJoin blocks=8, Coroutine/VirtualThread blocks=32) have NO throwing guard - a resolution smaller than the block grid yields integer block size 0 and writes 0 pixels. Tests pin that observable shortfall (writes.size == 0) rather than a typed exception. NaiveCoroutine has no block grid / no guard at all - its 'failure-path' test pins the behavioral contrast: it fully renders an 8x8 film that the block renderers leave empty. No flaky thread-race/interrupt tests written - the mid-render barrier path in ParallelRenderer cannot be triggered deterministically and its catch blocks remain uncovered by design.

AC#3 (equivalence): one 32x32 reference scene (32 divisible by every block grid + the 4-quarter parallel split) rendered through Sequential/ForkJoin/Parallel/NaiveCoroutine/Coroutine/VirtualThread. Uses a PositionalSingleRayRenderer (distinct color per (r,c)) so the comparison catches pixel-misplacement, not just count. Asserts pixel-exact map equality against the sequential reference; reference verified complete first.

Coverage (jacoco, before -> after):
- GridUtilities: 1282 missed instr / 0 covered -> 0 missed / 1282 covered (150/150 lines). Fully covered.
- renderer pkg: 916 missed / 321 covered instr -> 247 missed / 990 covered. ForkJoin, NaiveCoroutine, Sequential, Block now 0 missed; Coroutine & VirtualThread 1 missed instr each (the post-render film=null reset). Remaining 247 pkg misses are out of scope: SampledSingleRayRenderer (92), Renderer enum (66), SimpleSingleRayRenderer (34), and ParallelRenderer's TASK-5 interrupt/broken-barrier catch blocks (26, not deterministically reachable).

Verified: just test (./gradlew clean check) BUILD SUCCESSFUL - all tests + detekt green. Pre-existing unchecked-cast warnings in PlyReader.kt and GridStructuresTest.kt are unrelated.
<!-- SECTION:NOTES:END -->

## Final Summary

<!-- SECTION:FINAL_SUMMARY:BEGIN -->
Added tests for renderer strategies and GridUtilities tessellation (additive; no production change beyond a shared test matcher). AC#1: GridUtilitiesTest pins flat/smooth sphere tessellation structure across step pairs (3,3)(4,4)(5,3)(6,5) — triangle count 2*h*(v-1) exercising top/bottom pole caps + middle-ring loop, north/south pole vertices, all vertices on the unit sphere, smooth per-vertex radial normals (incl. pole=Normal.UP) vs flat single face normal; GridUtilities.kt now fully covered (1282->0 missed instr, 150/150 lines). AC#2: ForkJoin/Coroutine/NaiveCoroutine/VirtualThread each get a success test (block-aligned resolution fills every pixel) and a deterministic failure test. Implementer/reviewer confirmed a real renderer-family asymmetry on bad input: only ParallelRenderer throws (IllegalArgumentException); the block renderers silently write 0 pixels when resolution < block grid (Block.partitionIntoBlocks truncates blockHeight to 0), and NaiveCoroutine has no guard — the failure tests pin this genuine observable behaviour rather than asserting a false throw. No flaky thread-race tests (mid-render barrier catch blocks remain uncovered by design). AC#3: a 32x32 reference rendered through Sequential/ForkJoin/Parallel/NaiveCoroutine/Coroutine/VirtualThread is asserted pixel-exact equal (PositionalSingleRayRenderer gives a distinct color per coordinate so misplaced-pixel bugs are caught). Added Normal.shouldBeApprox to Fixture.kt. Reviewer independently re-ran jacoco (numbers verified) and traced production code to confirm the failure characterization is accurate. Verified via just test (clean check + detekt + jacoco) BUILD SUCCESSFUL. Committed as 78397ac. FOLLOW-UP surfaced (latent bug): the block renderers (ForkJoin/Coroutine/VirtualThread) silently underfill — 0 pixels for sub-block-grid resolutions, and truncate remainder rows/cols for non-divisible resolutions — while only ParallelRenderer guards; a divisibility/min-resolution guard or remainder handling is worth a separate task.
<!-- SECTION:FINAL_SUMMARY:END -->
