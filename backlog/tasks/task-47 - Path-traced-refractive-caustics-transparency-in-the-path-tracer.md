---
id: TASK-47
title: Path-traced refractive caustics (transparency in the path tracer)
status: In Progress
assignee:
  - '@claude'
created_date: '2026-06-24 08:23'
updated_date: '2026-06-24 09:02'
labels:
  - book-coverage
  - global-illumination
  - transparency
  - chapter-28
dependencies: []
references:
  - Chapter 28 Realistic Transparency _ Ray Tracing from the Ground Up.pdf
priority: medium
ordinal: 50000
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Transparent and Dielectric materials work only with the Whitted tracer; neither overrides IMaterial.pathShade, so they render black under PATH_TRACE and the ray tracer cannot produce refractive caustics: the bright, concentrated light refracted through a transparent object onto another surface (book section 28.9, Figure 28.42). The book notes caustics can only be rendered with path tracing (or photon mapping). The transmitted/reflected sampling already exists (PerfectTransmitter/FresnelTransmitter sampleF + isTir, PerfectSpecular/FresnelReflector sampleF), and Dielectric already applies Beer-Lambert color filtering (cfIn/cfOut) in its Whitted shade; this task wires that into a pathShade override.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Transparent.pathShade (and/or Dielectric.pathShade) spawns the reflected and transmitted bounces through world.tracer.trace(ray, depth+1), handles total internal reflection via the BTDF isTir test, and for Dielectric applies the cfIn/cfOut Beer-Lambert attenuation, consistent with the Whitted shade
- [x] #2 A new auto-discovered example scene reproduces book Figure 28.42: a red transparent sphere and a rectangle lit by an area/emissive light, with a visible caustic on the rectangle, using preferredTracer(PATH_TRACE)
- [x] #3 A transparent/dielectric object rendered with PATH_TRACE is no longer black and refracts the scene behind it
- [x] #4 pathShade logic (commonMain) is covered by frozen unit tests (cover-first, specs/testing.md); detekt and the full build stay green; the example scene is verified manually (expect noise; the book uses 256 samples/pixel)
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Cover-first: write frozen unit tests for Transparent.pathShade and Dielectric.pathShade (commonTest) using the fake IWorld/Tracer seams from TASK-46 tests. Assert reflected+transmitted bounces are spawned via tracer.trace(depth+1), TIR is handled (only reflected ray), and Dielectric applies cfIn/cfOut Beer-Lambert. Run them red against the default Color.BLACK pathShade.
2. PathTrace: override trace(ray, tmin, depth) to set tmin.value=sr.t (mirroring Whitted) so Dielectric's Beer-Lambert distance is correct under PATH_TRACE; cover with a frozen PathTrace test.
3. Implement Transparent.pathShade: spawn reflected + transmitted bounces via world.tracer.trace(ray, depth+1), handle isTir (only reflected), no direct Phong term — mirroring the non-super.shade part of Transparent.shade.
4. Implement Dielectric.pathShade: reuse fresnelContribution (Fresnel reflected/transmitted, TIR, Beer-Lambert) without the super.shade direct term — mirroring Reflective.pathShade dropping the direct term.
5. Add auto-discovered example scene RefractiveCaustic.kt (examples/globalillumination) reproducing Fig 28.42: red transparent sphere + rectangle + emissive area light, preferredTracer(PATH_TRACE).
6. Run ./gradlew clean check (just test): all tests + detekt green. Render the scene to verify non-black, refracts behind, coherent.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
Approach: mirrored the TASK-46 pathShade pattern (world.tracer.trace(ray, depth+1)) for transparency.

Production changes (commonMain):
- materials/Transparent.kt: added pathShade — spawns the perfect-specular reflected + perfect-transmitter transmitted bounces one level deeper (no Phong direct term, matching how Reflective.pathShade drops it). TIR via PerfectTransmitter.isTir adds only the reflected radiance, mirroring the GI block of the Whitted shade. Returns BLACK with no tracer.
- materials/Dielectric.kt: added pathShade = fresnelContribution(world, sr) — the same Fresnel-weighted reflected/transmitted + TIR + Beer-Lambert (cfIn/cfOut) the Whitted shade already trusts, minus super.shade.
- tracers/PathTrace.kt: overrode trace(ray, tmin, depth) to report the nearest hit distance via tmin (mirroring Whitted), routing the existing depth==0 averaging + tracePath through it. Dielectric Beer-Lambert needs that path length; previously the inherited default left tmin at Double.MAX_VALUE, which would extinguish a non-white filter. Single-arg trace now delegates to the tmin variant.

Tests (commonTest, cover-first, frozen — all RED against the default BLACK pathShade before impl, GREEN after; same fake IWorld/Tracer seams as the TASK-46 tests):
- materials/TransparentPathShadeTest.kt (5 specs): reflected term cr*kr*incoming + transmitted term; both rays traced at depth+1; reflected up / transmitted down directions; TIR traces only the reflected ray; BLACK without a tracer.
- materials/DielectricPathShadeTest.kt (7 specs): Fresnel split at normal incidence (reflected weight kr=0.04, transmitted weight kt/eta^2 — the radiance-compression 1/eta^2 factor, same as the Whitted shade); Beer-Lambert cfIn^2 over a path of length 2 via the tmin overload; both bounces at depth+1; TIR reflected-only; up/down directions; BLACK without a tracer. (My first pass naively expected kr+kt=1; corrected to the physical kr + kt/eta^2 after probing — the code matches the existing Whitted fresnelContribution, which the existing DielectricShadeTest only asserts as >0 for the same reason.)

Example scene (examples/**, coverage-excluded): examples/globalillumination/RefractiveCaustic.kt — red-tinted Dielectric glass sphere just above a white matte floor, a bright emissive ceiling panel (the caustic's light), and a softly self-lit warm back wall so the refraction-through is visible; preferredTracer(PATH_TRACE).

Manual verification (rendered, PATH_TRACE / FORK_JOIN / 720p, ~33s): a bright concentrated refractive caustic is clearly visible on the floor directly below the sphere; the sphere shows reddish refracted/reflected light against the warm backdrop (no longer the uniform black disk the default BLACK pathShade produced); scene is coherent (lit walls/floor via GI, emissive panel, expected Monte-Carlo noise — the book uses 256 spp; this tracer averages 100 paths/pixel without next-event estimation, so the caustic and refraction read noisy/dim, as the task anticipates). Verified the dielectric geometry independently by rendering the same scene under WHITTED (refracted highlight + light visible).

Full check: just test (./gradlew clean check) GREEN — all tests + detekt. The two compiler warnings (PlyReader.kt, GridStructuresTest.kt unchecked casts) are pre-existing and unrelated.
<!-- SECTION:NOTES:END -->
