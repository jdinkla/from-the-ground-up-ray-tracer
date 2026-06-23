---
id: TASK-36
title: Fix field-of-view changing with render resolution (sizeOfPixel not rescaled)
status: In Progress
assignee:
  - '@claude'
created_date: '2026-06-23 11:58'
updated_date: '2026-06-23 12:04'
labels: []
dependencies: []
ordinal: 39000
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Rendering the same scene at different resolutions changes the field of view (zoom), not just the pixel density. Lower resolutions zoom in (objects appear larger); higher resolutions zoom out. Confirmed with AmbientOccludedSphere at 1080p/720p/480p: the eye and horizon stay put (no parallax), only the FOV changes.

Root cause: the view-plane's visible world extent is sizeOfPixel x resolution, but ViewPlane.sizeOfPixel is a frozen constant 1.0 (ViewPlane.kt:10, private setter, never written). Context.adapt overrides world.viewPlane.resolution with the CLI/UI resolution (Context.kt:27) without rescaling sizeOfPixel, so the view-plane extent (and thus FOV) scales linearly with pixel count. The lenses build ray directions from sizeOfPixel x (pixel - 0.5 x res): Pinhole.kt:29-30, ThinLens, FishEye, Spherical.

Fix: when adapting resolution, rescale sizeOfPixel to preserve the scene's view-plane extent, i.e. keep sizeOfPixel x height invariant: sizeOfPixel_new = sizeOfPixel_old x (height_old / height_new). 16:9 aspect is fixed so this corrects both axes. Cleanest home is a method on ViewPlane (it owns the private setter) invoked from Context.adapt. 1080p output is unchanged (it is the default reference); all other resolutions change output by design.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Rendering a scene at 480p, 720p, 1080p, 1440p, 2160p yields the same field of view (same visible world extent / framing); only sampling density differs
- [x] #2 ViewPlane exposes a way to set resolution that rescales sizeOfPixel so sizeOfPixel x height is preserved relative to the pre-override value
- [x] #3 Context.adapt uses that mechanism instead of assigning resolution directly (Context.kt:27)
- [x] #4 1080p renders are byte-identical to before the change (default reference resolution, ratio 1.0)
- [x] #5 A characterization/unit test pins the FOV-invariance: sizeOfPixel x height (view-plane extent) is constant across resolutions for a given scene
- [x] #6 Full check is green: ./gradlew build (compile + test + detekt)
<!-- AC:END -->



## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Cover-first/TDD (this is a behaviour fix, so tests assert the corrected behaviour):
   - ViewPlaneTest: applyResolution scales sizeOfPixel inversely to the height change so sizeOfPixel*height (FOV) is invariant; lower res -> larger pixel (1080->720 => s=1.5), higher res -> smaller (1080->2160 => s=0.5); applying 1080 (reference) leaves s=1.0 (1080p byte-identical); a cross-resolution invariance test over all Predefined resolutions.
   - ContextTest: adapt() preserves the scene's design extent (sizeOfPixel*height) while switching resolution.
2. ViewPlane: add fun applyResolution(newResolution) that does sizeOfPixel *= resolution.height/newResolution.height then resolution = newResolution. Keep resolution setter public (ThinLensTest constructs a fixed-resolution view plane directly).
3. Context.adapt (Context.kt:27): replace 'world.viewPlane.resolution = resolution' with 'world.viewPlane.applyResolution(resolution)'.
4. Run ./gradlew build (test + detekt). Manually verify by re-rendering AmbientOccludedSphere at 480p/720p/1080p and confirming identical framing.
<!-- SECTION:PLAN:END -->
