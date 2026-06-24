---
id: TASK-43
title: >-
  Fix MultipleObjects.kt: zero-intensity light (ls=0.0) and off-camera objects
  render it 100% black
status: In Progress
assignee:
  - '@claude'
created_date: '2026-06-23 21:31'
updated_date: '2026-06-24 09:42'
labels:
  - examples
  - bug
  - audit
dependencies: []
priority: low
ordinal: 46000
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Surfaced by the TASK-38 audit and confirmed during TASK-39: MultipleObjects.kt renders 100% black under BOTH the WHITTED and MULTIPLE_OBJECTS tracers (pixel-verified: max channel 0 across all pixels), so it is a TRUE near-black render, not a tracer false-positive. Root cause is scene content: its only non-ambient light is pointLight(location = p(3,3,1)) with no ls, and the pointLight DSL default is ls = 0.0 (zero intensity); additionally the spheres appear to fall outside the camera frame given eye=p(0,0,200)/lookAt=p(50,0,0), so even the ambientLight(ls=0.5) term contributes nothing visible. Fix the scene so it renders a non-black image with its intended MULTIPLE_OBJECTS tracer (it already declares preferredTracer=MULTIPLE_OBJECTS as of TASK-39): give the point light a non-zero ls and/or reframe the camera so the objects are in view. This is example/scene content (JaCoCo-excluded) — verify by rendering the scene (just run / just swing) and by rerunning ./gradlew audit to confirm it drops off the near-black SUSPECT list.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 MultipleObjects.kt renders a visibly non-black image with its declared MULTIPLE_OBJECTS tracer (point light given non-zero intensity and/or camera reframed so objects are in view)
- [x] #2 The scene no longer appears on the ./gradlew audit near-black SUSPECT list; full build incl. detekt stays green
- [x] #3 Verified manually by rendering the scene (excluded zone): output is coherent, not black
<!-- AC:END -->



## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Confirm root cause: pointLight DSL defaults ls=0.0, so the scene's only non-ambient light is zero-intensity; verify camera framing via render.
2. Minimal scene-content fix: give the point light a non-zero ls (matching the YellowAndRedSphere/VariousObjects convention), and reframe camera if objects fall outside the frame.
3. Render MultipleObjects.kt with MULTIPLE_OBJECTS tracer; confirm output is coherent and non-black (inspect PNG pixel stats).
4. Run ./gradlew audit; confirm MultipleObjects.kt drops off the near-black SUSPECT list.
5. Run ./gradlew clean check (just test); confirm green incl. detekt.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
Root cause confirmed via a throwaway diagnostic (since removed): the true near-black cause was NOT primarily the zero-intensity light or framing, but an integer/float color-overload bug. The scene wrote materials as c(1,0,0)/c(0,1,0)/c(0,0,1) with Int literals, which bind to the c(Int,Int,Int) DSL overload (0-255, divides by 255) -> Color(0,0,0.00392), i.e. ~1/255 brightness. Diagnostic showed a sphere hit shading to Color(0,0,0.00297). Fix: write colors as Double literals c(1.0,0.0,0.0) etc. so they bind to the c(Double,Double,Double) overload (0.0-1.0). Also: the original pointLight had ls=0.0 (zero intensity, DSL default) and sat at p(3,3,1) among/behind the spheres -> gave it ls=3.0 and moved it to p(100,100,200) (front/above) so visible faces are diffusely lit. Reframed camera to eye=p(40,30,250)/lookAt=p(40,20,0) d=2000 to center all three spheres (the original eye=p(0,0,200)/lookAt=p(50,0,0) did frame them, but off-center and small). Verified by rendering at 720p with MULTIPLE_OBJECTS: coherent image of red/green/blue diffuse spheres, maxChannel=214, ~10.9% lit.
<!-- SECTION:NOTES:END -->
