---
id: TASK-41
title: 'Lens coverage: FishEye and Spherical cameras (DSL + scenes)'
status: In Progress
assignee:
  - '@claude'
created_date: '2026-06-23 19:15'
updated_date: '2026-06-23 19:36'
labels:
  - examples
  - coverage
  - dsl
dependencies: []
priority: low
ordinal: 44000
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
The TASK-38 audit reports the FishEye and Spherical lenses as uncovered: only Pinhole and ThinLens are reachable from the scene DSL (WorldScope exposes camera(...) -> Pinhole and thinLensCamera(...) -> ThinLens). FishEye already works and is tested (TASK-1); Spherical exists too. Add DSL camera methods so scenes can select these lenses, then add an example scene for each that shows the lens's characteristic distortion (wide FOV / full panorama). Unlike the geometric-primitive scenes, the DSL methods live in commonMain (testable core), so cover-first applies: write frozen unit tests for the new WorldScope methods before wiring scenes.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 WorldScope gains DSL methods to build a FishEye camera and a Spherical camera (mirroring camera(...) / thinLensCamera(...)), exposing the lens-specific parameters (e.g. FishEye field of view)
- [ ] #2 Frozen unit tests in commonTest cover the new WorldScope methods (camera/lens correctly constructed), written before the scenes and passing
- [ ] #3 An example scene exists for each lens; re-running ./gradlew audit shows FishEye and Spherical no longer in 'Uncovered classes' (Lenses)
- [ ] #4 Both scenes render to non-near-black images (verified manually) and are not flagged Suspect/Failed by the audit
- [ ] #5 Full build stays green: ./gradlew build (compile + test + detekt) passes
<!-- AC:END -->
