---
id: TASK-50
title: Chapter 27 simple-transparency example scenes
status: In Progress
assignee:
  - '@claude'
created_date: '2026-06-24 08:24'
updated_date: '2026-06-24 10:09'
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
- [ ] #1 New WorldDefinition scenes exist for: air bubble in water (transparent sphere with relative eta below 1, e.g. 0.75, showing TIR from the outside as a dark/mirror ring; book Figure 27.15)
- [ ] #2 A reflective + transparent sphere over a checker plane at proper glass ior 1.5 (book Figure 27.12/27.13)
- [ ] #3 A transparent torus (book Figure 27.29 / exercise 27.8; note the characteristic black strips) and a transparent ellipsoid (scaled Sphere via Instance; book Figures 27.24/27.28)
- [ ] #4 Transparent compound objects: a solid cylinder, a thick ring and a hemispherical bowl, all showing total internal reflection off their inside surfaces (book Figure 27.19)
- [ ] #5 Each scene auto-registers (classgraph), renders without errors at a sensible max recursion depth with a non-black background, and is verified manually by rendering (examples/** coverage-excluded); detekt and the full build stay green
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
