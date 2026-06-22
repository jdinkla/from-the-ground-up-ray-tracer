---
id: TASK-22
title: Stereo camera
status: In Progress
assignee:
  - '@claude'
created_date: '2026-06-22 09:41'
updated_date: '2026-06-22 17:44'
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
- [ ] #1 A stereo camera renders a left/right eye pair (side-by-side and/or anaglyph) for a scene
- [ ] #2 Stereo camera is selectable from the Builder DSL
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
