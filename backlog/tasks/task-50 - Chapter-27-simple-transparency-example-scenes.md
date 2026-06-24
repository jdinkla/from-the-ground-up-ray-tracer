---
id: TASK-50
title: Chapter 27 simple-transparency example scenes
status: To Do
assignee: []
created_date: '2026-06-24 08:24'
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
