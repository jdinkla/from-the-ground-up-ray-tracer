---
id: TASK-41
title: 'Lens coverage: FishEye and Spherical cameras (DSL + scenes)'
status: In Progress
assignee:
  - '@claude'
created_date: '2026-06-23 19:15'
updated_date: '2026-06-23 19:46'
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
- [x] #1 WorldScope gains DSL methods to build a FishEye camera and a Spherical camera (mirroring camera(...) / thinLensCamera(...)), exposing the lens-specific parameters (e.g. FishEye field of view)
- [x] #2 Frozen unit tests in commonTest cover the new WorldScope methods (camera/lens correctly constructed), written before the scenes and passing
- [x] #3 An example scene exists for each lens; re-running ./gradlew audit shows FishEye and Spherical no longer in 'Uncovered classes' (Lenses)
- [x] #4 Both scenes render to non-near-black images (verified manually) and are not flagged Suspect/Failed by the audit
- [x] #5 Full build stays green: ./gradlew build (compile + test + detekt) passes
<!-- AC:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
DSL: added WorldScope.fishEyeCamera(maxPsi) and sphericalCamera(maxLambda, maxPsi), mirroring camera()/thinLensCamera(); no view-plane distance arg (these lenses normalise the plane and ignore d). Exposed the FOV params by promoting FishEye.maxPsi and Spherical.maxLambda/maxPsi from private to public var (same pattern as ThinLens.d/f/lensRadius); class defaults unchanged so FishEyeTest stays frozen.
Frozen tests (commonTest) written before the scenes: WorldScopeTest gained 'fishEyeCamera selects a FishEye...' and 'sphericalCamera selects a Spherical...' asserting lens type + FOV fields.
Scenes: FishEyeScene.kt and SphericalScene.kt — camera at the centre of a 12-sphere ring + ground plane so the wide FOV is visible. Eyeballed at 720p: FishEye gives the classic circular image (black corners), Spherical unrolls a 360deg panorama.

IN-SCOPE DISCOVERY (prerequisite, not creep): the single-ray render path threw 'Lens returned no ray' on any null ray, so a fisheye (which returns null for pixels outside the image circle, by design) was unrenderable. Fixed both SimpleSingleRayRenderer (null -> Color.BLACK) and SampledSingleRayRenderer (skip null samples; ColorAccumulator.average divides by the valid count, antialiasing the circle edge; all-null pixel -> black). This matches the documented ILens contract ('a null return ... should be skipped'). ZERO behavior change for existing scenes: Pinhole/ThinLens/Spherical never return null, only FishEye does. Updated the two renderer tests that had pinned the old throw behavior (a deliberate behavior change, not a refactor) and added a partial-null averaging test.
Verified: ./gradlew audit -> Lenses 2/4 -> 4/4, neither scene Suspect/Failed; ./gradlew build (detekt + tests) green.
<!-- SECTION:NOTES:END -->

## Final Summary

<!-- SECTION:FINAL_SUMMARY:BEGIN -->
Added fishEyeCamera/sphericalCamera DSL methods (exposing FOV via now-public maxPsi/maxLambda) + FishEyeScene/SphericalScene examples, taking Lenses coverage 2/4 -> 4/4. Required fixing the single-ray renderers to treat a null lens ray as background instead of throwing (fisheye was previously unrenderable) — safe, no change for existing lenses. Cover-first frozen tests for the DSL methods and the renderer null-ray behavior; ./gradlew build green; both scenes eyeballed at 720p.
<!-- SECTION:FINAL_SUMMARY:END -->
