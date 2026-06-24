---
id: TASK-49
title: Camera exposure time + camera-inside-transparent-sphere example
status: In Progress
assignee:
  - '@claude'
created_date: '2026-06-24 08:23'
updated_date: '2026-06-24 09:58'
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
- [ ] #1 Camera gains an exposureTime property (default 1.0) that multiplies the radiance returned per primary ray; existing scenes are unaffected at the default
- [ ] #2 exposureTime is configurable from the scene DSL camera block
- [ ] #3 A new auto-discovered example scene places the camera inside a transparent (Dielectric) sphere or ellipsoid with a surrounding environment (ring of spheres + checker plane, book Figure 28.34) using a reduced exposureTime so the interior view is not washed out
- [ ] #4 Camera/exposure logic (commonMain/jvmMain) is covered by a frozen unit test where applicable (cover-first, specs/testing.md); detekt and the full build stay green; the scene is verified manually
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
