---
id: TASK-50
title: Chapter 27 simple-transparency example scenes
status: Done
assignee:
  - '@claude'
created_date: '2026-06-24 08:24'
updated_date: '2026-06-24 10:20'
labels:
  - book-coverage
  - examples
  - transparency
  - chapter-27
dependencies: []
references:
  - Chapter 27 Simple Transparency _ Ray Tracing from the Ground Up.pdf
priority: low
ordinal: 53000
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
The Transparent material (ch. 27, PerfectSpecular BRDF + PerfectTransmitter BTDF, Whitted tracer) is implemented and exercised by TransparentSpheres and the old World35/71/71b scenes, but those use spheres and aligned boxes at a very weak ior (~1.02). Several distinctive ch. 27 demonstrations have no example scene. Scenes are auto-discovered WorldDefinition objects under src/examples (coverage-excluded; verify by rendering). Follow precedent of TASK-40 (one task, several related scenes). All required primitives already exist (Sphere, Torus, Instance for scaling, SolidCylinder, ThickRing, Bowl).
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 New WorldDefinition scenes exist for: air bubble in water (transparent sphere with relative eta below 1, e.g. 0.75, showing TIR from the outside as a dark/mirror ring; book Figure 27.15)
- [x] #2 A reflective + transparent sphere over a checker plane at proper glass ior 1.5 (book Figure 27.12/27.13)
- [x] #3 A transparent torus (book Figure 27.29 / exercise 27.8; note the characteristic black strips) and a transparent ellipsoid (scaled Sphere via Instance; book Figures 27.24/27.28)
- [x] #4 Transparent compound objects: a solid cylinder, a thick ring and a hemispherical bowl, all showing total internal reflection off their inside surfaces (book Figure 27.19)
- [x] #5 Each scene auto-registers (classgraph), renders without errors at a sensible max recursion depth with a non-black background, and is verified manually by rendering (examples/** coverage-excluded); detekt and the full build stay green
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Study book Ch.27 figures (read PDF) + existing transparent/dielectric scenes (TransparentSpheres, World35/71, GlassSphere) for DSL conventions: transparent(ior,ks,kt,kr,exp), checker floor via svMatte+PlaneChecker, backdrop plane for non-black background, preferredTracer(WHITTED), default maxDepth=5.
2. Add AirBubbleInWater.kt (AC#1): transparent sphere ior=0.75, kr=0.1 -> dark TIR ring (Fig 27.15a); red sphere behind, teal backdrop, white checker floor.
3. Add ReflectiveAndTransparentSpheres.kt (AC#2): glass sphere ks=0.5/exp=2000/ior=1.5/kr=0.1/kt=0.9 + a reflective sphere over a checker plane (Fig 27.12/27.13).
4. Add TransparentTorus.kt (AC#3a): torus ior=1.5 with a red sphere inside, black strips (Fig 27.29). Add TransparentEllipsoid.kt (AC#3b): scaled Sphere via Instance, ior=0.75 (Fig 27.24/27.28).
5. Add TransparentCompoundObjects.kt (AC#4): solidCylinder + thickRing + bowl, ior=1.5, red spheres behind, TIR off inside surfaces (Fig 27.19).
6. Render each scene at 720p WHITTED, confirm non-black/coherent/effect visible; clean up PNGs. Run ./gradlew clean check (detekt). Record notes.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
Implemented 5 new auto-discovered WorldDefinition scenes under src/examples/kotlin/net/dinkla/raytracer/examples/materials/transparent/ (coverage-excluded; verified by rendering, no unit tests per the cover-first exception for examples/**).

Files added:
- AirBubbleInWater.kt (AC#1, Fig 27.15): transparent sphere, relative eta=0.75, kr=0.1, kt=0.9 -> TIR from the outside reads as a mirror ring (NOTE: this codebase's Transparent.shade follows Suffern Listing 27.4 exactly, where TIR does FULL mirror reflection 'l += cr' ignoring kr; so it reproduces Fig 27.15(b) mirror ring, not the (a) dark ring which came from the book's 'incorrect' kr-on-TIR variant. AC wording 'dark/mirror ring' is satisfied). Red sphere behind, teal backdrop plane, white checker floor.
- ReflectiveAndTransparentSpheres.kt (AC#2, Figs 27.12/27.13): glass sphere with exact Listing 27.5 params (ks=0.5, exp=2000, ior=1.5, kr=0.1, kt=0.9) beside a reflective mirror sphere over a checker plane. Checker is refracted/inverted through glass; bright TIR rim + central dark disk visible.
- TransparentTorus.kt (AC#3a, Fig 27.29): glass torus (ior=1.5) instanced and rotated 90deg about x to stand it up, red sphere in the hole; black strips visible at top.
- TransparentEllipsoid.kt (AC#3b, Figs 27.24/27.28): unit Sphere scaled (2.2,1.1,1.1) via Instance, eta=0.75; flattened ellipsoid with refracted red sphere, silhouette TIR strip + elongated highlights.
- TransparentCompoundObjects.kt (AC#4, Fig 27.19): solidCylinder + thickRing (stood up) + bowl, all ior=1.5, red spheres behind/inside; all three show TIR dark regions on inside surfaces.

DSL notes: no DSL hook exists for background or maxDepth. Non-black background achieved with a matte backdrop plane (established convention, cf. TransparentSpheres 'sky' plane, GlassSphere 'back' plane). World default ViewPlane.maximalRecursionDepth=5 is already a sensible depth for transparency (book uses max_depth=5 for Fig 27.13c) -> no override needed. All Double color literals (c(...)) to avoid the Int-overload trap. preferredTracer(WHITTED) on each.

Verification: rendered each at 720p with --tracer=WHITTED; all non-black, coherent, effect visible (see per-scene render report). PNGs cleaned up from parent workspace dir.

CHECK: ./gradlew clean check -> BUILD SUCCESSFUL (detekt + all tests green). Two pre-existing unchecked-cast warnings (PlyReader.kt, GridStructuresTest.kt) unrelated to this change.
<!-- SECTION:NOTES:END -->

## Final Summary

<!-- SECTION:FINAL_SUMMARY:BEGIN -->
Added five auto-discovered Chapter 27 simple-transparency example scenes under src/examples/.../materials/transparent/ (no production code touched): AirBubbleInWater.kt (transparent sphere relative eta 0.75 -> mirror TIR ring from outside, Fig 27.15; this codebase's Transparent.shade follows Listing 27.4 verbatim so TIR is a full mirror ring, not the book's deliberately-incorrect dark-ring variant), ReflectiveAndTransparentSpheres.kt (glass sphere with Listing 27.5 params ks=0.5/exp=2000/ior=1.5/kr=0.1/kt=0.9 + mirror sphere over a checker plane, Figs 27.12/27.13), TransparentTorus.kt (glass torus, black strips, Fig 27.29), TransparentEllipsoid.kt (non-uniformly Instance-scaled sphere, Figs 27.24/27.28), and TransparentCompoundObjects.kt (solidCylinder + thickRing + bowl all showing inside-surface TIR, Fig 27.19). All preferredTracer WHITTED, maxDepth 5, non-black backdrop. Verified by manager self-review: ./gradlew clean check green (detekt), ./gradlew audit health-renders all 87 scenes with all five new scenes auto-registered, non-black (suspect list empty), and none in the failed list (only pre-existing World61 missing-ply). Committed 38905b7.
<!-- SECTION:FINAL_SUMMARY:END -->
