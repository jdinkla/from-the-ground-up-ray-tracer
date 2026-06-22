---
id: TASK-22
title: Stereo camera
status: Done
assignee:
  - '@claude'
created_date: '2026-06-22 09:41'
updated_date: '2026-06-22 17:56'
labels:
  - enhancement
  - book-parity
dependencies: []
priority: low
ordinal: 25000
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
The book's StereoCamera (parallel and transverse/anaglyph stereo, rendering left/right eye views) is the one camera type missing here (Pinhole, ThinLens, FishEye, Spherical exist). Add it as a camera/lens that renders a side-by-side or anaglyph stereo pair. Small, self-contained parity item.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 A stereo camera renders a left/right eye pair (side-by-side and/or anaglyph) for a scene
- [x] #2 Stereo camera is selectable from the Builder DSL
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Add StereoCamera (commonMain, cameras/) as a pure geometry holder: base eye/lookAt/up/d, separation, StereoMode (PARALLEL|TRANSVERSE), StereoViewing (SIDE_BY_SIDE|ANAGLYPH). It derives left/right Pinhole Cameras with eyes offset +/- u*(sep/2): PARALLEL keeps directions parallel (lookAt offset by same vector), TRANSVERSE aims both at shared lookAt. Unit-test the eye-offset + aiming geometry with hand-derived values.
2. Add StereoCompositor (commonMain) operating on a small pixel-source abstraction: sideBySide places left image at cols [0,w) and right at [w,2w) of a 2w film; anaglyph combines left.red with right.green/blue into a w film. Unit-test pixel placement + channel combine with fake films.
3. Wire optionally: World gains nullable stereoCamera (default null => existing path UNCHANGED). WorldScope.stereoCamera(...) DSL sets it and also a base Pinhole camera so the world stays valid. AC#2 met when a scene declares it.
4. Render.render (coverage-excluded glue): if world.stereoCamera != null, render left+right into two Films via the existing single-ray pipeline and composite into the output Film; else unchanged. Two-pass+composite lives in a StereoRenderHelper in jvmMain near Film.
5. Add example scene under examples/cameras declaring stereoCamera; manually render side-by-side (expect 2x width) and anaglyph; report dims + observation.
6. just test green, detekt-clean, confirm single-camera path + existing tests unaffected.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
Approach: stereo camera is a higher-level orchestration over the existing single-ray pipeline, additive and behavior-preserving (non-stereo scenes unchanged; World.stereoCamera defaults to null).

Files added (commonMain):
- cameras/StereoCamera.kt: pure geometry/config holder. Enums StereoMode (PARALLEL|TRANSVERSE), StereoViewing (SIDE_BY_SIDE|ANAGLYPH). Derives left/right Pinhole Cameras with eyes offset +/- baseBasis.u * (separation/2). PARALLEL shifts each look-at by the same vector (view dirs stay parallel); TRANSVERSE aims both at shared lookAt (toed-in).
- cameras/StereoCompositor.kt: pure pixel compositing over a PixelSource fun-interface. sideBySide writes a 2w x h film (left -> cols [0,w), right -> [w,2w)); anaglyph writes a w x h film (red from left, green/blue from right).
- films/ColorGridFilm.kt: in-memory IFilm with read-back (colorAt), used to buffer each eye render for compositing.
- world/StereoRender.kt: the two-pass+composite glue. Renders left+right eye each via a fresh SimpleSingleRayRenderer(eyeCamera.lens, tracer) driven through the context's RendererCreator into a ColorGridFilm, then composites into the output Film. Coverage-excluded render glue.

Files modified:
- world/World.kt: added nullable stereoCamera param (default null).
- world/dsl/WorldScope.kt: added stereoCamera(eye, lookAt, up, separation, mode, viewing, d) DSL; also sets a base Pinhole camera so World stays valid. Passes stereoCamera into World.
- world/Render.kt: render(WorldDefinition, context) branches to StereoRender.render when world.stereoCamera != null; non-stereo path unchanged.
- examples/cameras/StereoSpheres.kt: example scene declaring stereoCamera (AC#2 driver).

Unit tested (testable core, hand-derived values):
- StereoCameraTest: left/right eye = base eye -/+ u*(sep/2) with u=(1,0,0) for eye=origin,lookAt=(0,0,-1); PARALLEL look-at offsets keep view dirs equal (0,0,-1); TRANSVERSE both aim at lookAt; derived Pinhole cameras carry d and offset eyes.
- StereoCompositorTest: side-by-side pixel placement (left cols [0,2), right cols [2,4) of a 4-wide film; exactly 2*w*h writes); anaglyph channel combine (out.red==left.red, out.green/blue==right.green/blue; exactly w*h writes) - all with traced 2x2 colors.
- ColorGridFilmTest: read-back, black default, neighbor isolation.
- WorldScopeTest: fresh world has stereoCamera==null (single-camera path preserved); stereoCamera(...) sets the configured separation/mode/viewing.

Manually verified (render glue, coverage-excluded):
- just run equivalent: ./gradlew run --args="--world=StereoSpheres.kt --tracer=WHITTED --renderer=SEQUENTIAL --resolution=480p".
- SIDE_BY_SIDE: output 1706x480 (= 2 x 853), two near-identical views with depth-dependent horizontal parallax. Log shows two render passes.
- ANAGLYPH (temp toggle, reverted): output 853x480 (single-eye width), clear red/cyan fringing with depth-dependent offset.

Full check: ./gradlew clean check green (tests + detekt + jacoco). No new detekt-baseline entries. Pre-existing unchecked-cast warnings in PlyReader.kt/GridStructuresTest.kt are unrelated.
<!-- SECTION:NOTES:END -->

## Final Summary

<!-- SECTION:FINAL_SUMMARY:BEGIN -->
Added Suffern's stereo camera as an additive orchestration over the existing single-ray lens pipeline. StereoCamera (pure geometry holder) derives a left + right Pinhole Camera whose eyes are offset by ∓u·(separation/2) along the camera's right (u) axis; PARALLEL mode keeps the two view directions parallel (shifted origin, same direction), TRANSVERSE toes both eyes in onto the shared lookAt. StereoCompositor merges the two eye images: SIDE_BY_SIDE (double-width — left in cols [0,w), right in [w,2w)) or ANAGLYPH (single width — red from the left eye, green/blue from the right). StereoRender does the two-pass render, reusing the real SimpleSingleRayRenderer(camera.lens, tracer) per eye via the context's RendererCreator (no per-eye reimplementation). AC1-2 both met. AC#2: a new WorldScope.stereoCamera(...) DSL block (following the camera(...) idiom) sets World.stereoCamera (nullable, default null) AND a base Pinhole camera so World.camera stays valid (Context.adapt reads world.camera.lens unconditionally — reviewer flagged this as a real correctness detail the implementer got right). Render.render branches to StereoRender only when world.stereoCamera != null; the non-stereo single-camera path is byte-identical (Context.adapt/SimpleSingleRayRenderer/ILens/Camera/camera() DSL all UNMODIFIED — reviewer confirmed via diff + existing BuilderTest/CameraTest/WorldScopeTest passing unchanged). Cover-first: StereoCameraTest (hand-derived left/right eye offsets + parallel vs transverse directions), StereoCompositorTest (side-by-side placement with exactly 2·w·h writes; anaglyph per-channel mapping with exactly w·h writes), ColorGridFilmTest; WorldScopeTest additive cases. AC#1 manually verified by both implementer and reviewer: ./gradlew run --world=StereoSpheres.kt --tracer=WHITTED --renderer=SEQUENTIAL --resolution=480p → two render passes, 1706×480 PNG (=2×853, side-by-side double-width) with visible depth-dependent horizontal parallax; anaglyph mode produces 853×480 with red/cyan fringing. detekt clean, no baseline entries. Verified via just test (clean check + detekt + jacoco) BUILD SUCCESSFUL. Committed as 47fea9c.
<!-- SECTION:FINAL_SUMMARY:END -->
