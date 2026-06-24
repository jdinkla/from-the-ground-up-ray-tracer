---
id: TASK-48
title: 'Hybrid GlobalTrace tracer: direct light sampling + path-traced indirect'
status: In Progress
assignee:
  - '@claude'
created_date: '2026-06-24 08:23'
updated_date: '2026-06-24 09:25'
labels:
  - book-coverage
  - global-illumination
  - tracers
  - chapter-26
dependencies: []
references:
  - Chapter 26 Global Illumination _ Ray Tracing from the Ground Up.pdf
priority: low
ordinal: 51000
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Pure path tracing is very noisy when light sources are small, because few random paths hit a light (book section 26.4, Figures 26.7/26.10/26.12). The book describes a hybrid tracer (GlobalTrace) that computes direct illumination by sampling the lights (as in ch. 18 area lighting) and uses path tracing only for the indirect illumination, giving far less noise for the same sample count. This needs a new tracer plus material global_shade methods (Listings 26.6 Emissive, 26.7 Matte, 26.8 Reflective) that avoid double-counting the direct light (book Figure 26.11 radiance-flow rules). Today only the pure PathTrace tracer exists (Tracers enum: WHITTED, AREA, MULTIPLE_OBJECTS, PATH_TRACE). Advanced / optional book feature.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 A new GLOBAL_TRACE entry in the Tracers enum maps to a tracer that returns direct illumination by sampling the lights at the first bounce and path-traced indirect illumination for deeper bounces, without counting direct light twice
- [x] #2 Matte and Emissive (and optionally Reflective) gain a globalShade method following book Listings 26.6 to 26.8
- [x] #3 An example Cornell-box scene rendered with GLOBAL_TRACE shows markedly less noise in the direct illumination than the same scene under PATH_TRACE at equal samples (book Figure 26.12)
- [x] #4 New tracer/material logic (commonMain) is covered by frozen unit tests (cover-first, specs/testing.md); detekt and the full build stay green; the scene is verified manually
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Add globalShade to IMaterial (default BLACK) following book Fig 26.11 radiance-flow rules.
2. Matte.globalShade (Listing 26.7): at sr.depth==0 add direct area-light sampling (areaLightShade) + one indirect path bounce; deeper bounces only the indirect path term (reuse pathShade body).
3. Emissive.globalShade (Listing 26.6): return BLACK when sr.depth==1 (avoid double count), else front-face test -> le, else BLACK.
4. Optional Reflective.globalShade (Listing 26.8): same as pathShade (mirror has no direct term).
5. GlobalTrace tracer: mirror PathTrace but call material.globalShade and set sr.depth; primary-level averaging over numSamples.
6. Add GLOBAL_TRACE to Tracers enum.
7. Cover-first frozen unit tests (commonMain seams): GlobalTraceTest, MatteGlobalShadeTest, EmissiveGlobalShadeTest, ReflectiveGlobalShadeTest, using existing fake IWorld/Tracer/light seams.
8. Add CornellBoxGlobal.kt example (AreaLight + emissive panel, preferredTracer(GLOBAL_TRACE)); render to verify non-black/coherent and compare noise vs PATH_TRACE.
9. just test (clean check) green; render-verify the scene.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
Implementation (commonMain + examples):
- IMaterial.globalShade default BLACK (additive seam, mirrors pathShade).
- GlobalTrace tracer (tracers/GlobalTrace.kt): clone of PathTrace but calls material.globalShade and sets sr.depth; primary-level averaging over numSamples; GLOBAL_TRACE added to Tracers enum.
- Matte.globalShade (Listing 26.7): depth 0 = direct (globalDirect) + indirect (pathShade); depth>0 = indirect only. globalDirect is a NEW private direct-sampling loop that uses the LIGHT's emitted radiance (light.getLightMaterial().getLe), NOT the receiving surface's getLe. This deliberately bypasses the pre-existing AreaLight.l quirk (it reads sr.material.getLe, the receiver, making AREA lights ~20x too dim); fixing AreaLight.l itself was out of scope and would break the frozen MatteAreaLightShadeTest + all AREA scenes, so I left it untouched and gave GlobalTrace its own correct direct term. globalDirect omits ambient (global tracer integrates indirect directly).
- Emissive.globalShade (Listing 26.6): BLACK at depth==1 (avoid double-count), else front-face le, else BLACK.
- Reflective.globalShade (Listing 26.8): delegates to pathShade (mirror has no direct term).
- @Suppress(TooManyFunctions) on Matte (now 13 fns; same convention as AreaLight/ObjectsScope).

Example: examples/globalillumination/CornellBoxGlobal.kt, preferredTracer(GLOBAL_TRACE). Ceiling light declared twice: a RectangleLight AreaLight (lights{}) for low-noise direct sampling + an emissive rectangle object so indirect bounces (depth>=2) still see it.

Cover-first frozen tests: tracers/GlobalTraceTest, materials/{MatteGlobalShadeTest, EmissiveGlobalShadeTest, ReflectiveGlobalShadeTest}. Frozen MatteAreaLightShadeTest + MattePathShadeTest still pass unchanged.

Manual verification (temporary jvmTest render probe, since examples/** is coverage-excluded; probe deleted after): rendered CornellBoxGlobal under GLOBAL_TRACE vs PATH_TRACE at equal samples=16, res 240p. Direct-lit floor patch coefficient-of-variation (noise proxy): GLOBAL 1.78 vs PATH 3.28 at identical floor mean luminance (0.065) -> ~half the noise (Fig 26.12). At 100 samples GLOBAL CoV 0.65 (converged). PNGs confirmed coherent: smooth red/green walls, lit floor, two boxes, bright ceiling panel; PATH_TRACE at 16 samples is pure salt-and-pepper noise.
<!-- SECTION:NOTES:END -->
