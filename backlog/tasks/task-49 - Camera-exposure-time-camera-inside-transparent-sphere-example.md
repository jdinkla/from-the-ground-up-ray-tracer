---
id: TASK-49
title: Camera exposure time + camera-inside-transparent-sphere example
status: Done
assignee:
  - '@claude'
created_date: '2026-06-24 08:23'
updated_date: '2026-06-24 10:06'
labels:
  - book-coverage
  - cameras
  - transparency
  - chapter-28
dependencies: []
references:
  - Chapter 28 Realistic Transparency _ Ray Tracing from the Ground Up.pdf
priority: low
ordinal: 52000
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
The book renders views from inside a transparent sphere/ellipsoid (book section 28.6.3, Figures 28.34, 28.50). Inside a dense medium the radiance carried along each primary ray is scaled by (eta_in/eta_out) squared as it crosses the surface (about 5.86 for diamond), so interior views are washed out unless camera exposure is reduced. The book gives the Camera base class an exposure_time member (set to 1/eta^2 when the camera is inside, default 1.0). Our Camera (cameras/Camera.kt) has no exposure control.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Camera gains an exposureTime property (default 1.0) that multiplies the radiance returned per primary ray; existing scenes are unaffected at the default
- [x] #2 exposureTime is configurable from the scene DSL camera block
- [x] #3 A new auto-discovered example scene places the camera inside a transparent (Dielectric) sphere or ellipsoid with a surrounding environment (ring of spheres + checker plane, book Figure 28.34) using a reduced exposureTime so the interior view is not washed out
- [x] #4 Camera/exposure logic (commonMain/jvmMain) is covered by a frozen unit test where applicable (cover-first, specs/testing.md); detekt and the full build stay green; the scene is verified manually
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Add var exposureTime: Double = 1.0 to Camera (cameras/Camera.kt).
2. Multiply traced radiance by exposureTime at the shared single-ray seam: add exposureTime ctor param (default 1.0) to SimpleSingleRayRenderer and SampledSingleRayRenderer; thread world.camera.exposureTime through Context.singleRayRenderer and StereoRender so every camera/lens type honours it once.
3. Expose exposureTime in the DSL camera(...) block (WorldScope) so scenes can set it; default 1.0 keeps existing scenes byte-identical.
4. Cover-first frozen unit tests: defaults to 1.0 (returned radiance unchanged) and exposureTime scales the returned radiance, on both SimpleSingleRayRenderer and SampledSingleRayRenderer (and a CameraTest for the default property).
5. Add auto-discovered example scene (camera inside a Dielectric sphere, ring of spheres + checker plane, Fig 28.34) with reduced exposureTime and preferredTracer WHITTED. Verify by rendering.
6. just test (./gradlew clean check) green; render the new scene and confirm interior not washed out.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
Approach: added a single shared exposureTime seam rather than per-lens code.

Files changed:
- commonMain/cameras/Camera.kt: new 'var exposureTime: Double = 1.0' (the multiplier per primary ray, documented with the 1/eta^2 rationale).
- commonMain/renderer/SimpleSingleRayRenderer.kt: new exposureTime ctor param (default 1.0); multiplies the traced colour (tracer.trace(ray,0) * exposureTime).
- commonMain/renderer/SampledSingleRayRenderer.kt: new exposureTime ctor param (default 1.0); applied to the per-pixel mean (color.average * exposureTime) so it scales every sample uniformly.
- commonMain/world/Context.kt: threads world.camera.exposureTime into both single-ray renderers.
- commonMain/world/StereoRender.kt: threads camera.exposureTime into SimpleSingleRayRenderer so stereo honours it too.
- commonMain/world/dsl/WorldScope.kt: camera(...) gains an exposureTime param (default 1.0) applied via Camera(...).apply { this.exposureTime = ... }.
- examples/.../materials/dielectric/InsideTransparentSphere.kt: new auto-discovered scene 'InsideTransparentSphere.kt' (camera at centre of a diamond-IOR=2.42 dielectric sphere, ring of 8 Phong spheres on a checker plane, preferredTracer WHITTED, exposureTime = 1/2.42^2 ~= 0.17).

Why this seam: every camera/lens type (Pinhole/ThinLens/FishEye/Spherical) produces its per-ray colour through ISingleRayRenderer, so multiplying there makes ALL camera types honour exposureTime with one change and no per-lens duplication. Default 1.0 is a no-op, so existing scenes are byte-identical.

Tests (cover-first, frozen):
- CameraTest: 'defaults exposureTime to 1.0 so existing scenes are unaffected'.
- SimpleSingleRayRendererTest: default 1.0 leaves colour unchanged; exposureTime=0.25 scales radiance (0.8/0.4/0.2 -> 0.2/0.1/0.05).
- SampledSingleRayRendererTest: default 1.0 unchanged; exposureTime=0.5 halves the averaged radiance.

Verification: 'just test' (./gradlew clean check, tests+detekt+jacoco) green. Manually rendered InsideTransparentSphere.kt via 'just run --world=InsideTransparentSphere.kt --tracer=WHITTED --resolution=720p': at exposureTime=1/eta^2 the interior view (refracted/curved checker floor + ring of spheres seen through the enclosing sphere) reads at normal brightness; a control render with exposureTime temporarily forced to 1.0 is visibly washed out (blown-out whites, over-saturated colours), confirming the compensation works end-to-end. Verification PNGs removed afterwards. Two pre-existing compiler warnings (PlyReader.kt unchecked cast, GridStructuresTest.kt) are unrelated to this change.
<!-- SECTION:NOTES:END -->

## Final Summary

<!-- SECTION:FINAL_SUMMARY:BEGIN -->
Added a Camera.exposureTime property (Suffern's exposure_time, default 1.0) that scales the radiance per primary ray, applied once at the shared single-ray renderer seam (SimpleSingleRayRenderer multiplies the traced color; SampledSingleRayRenderer multiplies the post-average pixel mean), threaded from camera through Context (both branches) and StereoRender so every camera/lens type honours it with no per-lens duplication. Default 1.0 is an exact IEEE identity no-op (Color*1.0), so existing scenes render byte-identically. Exposed exposureTime in the DSL camera(...) block. Added auto-discovered example InsideTransparentSphere.kt: camera inside a diamond-IOR (2.42) Dielectric sphere looking out at a ring of 8 spheres on a checker plane (book Fig 28.34, WHITTED), exposureTime=1/eta^2 (~0.17) so the (eta_in/eta_out)^2 radiance scaling does not wash out the interior view. Frozen cover-first tests on Camera default and both renderers (default no-op + scaling). Verified: ./gradlew clean check green, reviewer PASS (confirmed no-op identity, single scalar multiply in the right place, tests pin behavior, detekt baseline untouched), scene rendered legibly while a control at exposureTime=1.0 was visibly washed out. Committed 3ea5937. Follow-up nit: thinLensCamera/fishEyeCamera/sphericalCamera DSL helpers don't yet expose exposureTime (out of scope; renderer seam still honours it for any camera).
<!-- SECTION:FINAL_SUMMARY:END -->
