---
id: TASK-47
title: Path-traced refractive caustics (transparency in the path tracer)
status: To Do
assignee: []
created_date: '2026-06-24 08:23'
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
- [ ] #1 Transparent.pathShade (and/or Dielectric.pathShade) spawns the reflected and transmitted bounces through world.tracer.trace(ray, depth+1), handles total internal reflection via the BTDF isTir test, and for Dielectric applies the cfIn/cfOut Beer-Lambert attenuation, consistent with the Whitted shade
- [ ] #2 A new auto-discovered example scene reproduces book Figure 28.42: a red transparent sphere and a rectangle lit by an area/emissive light, with a visible caustic on the rectangle, using preferredTracer(PATH_TRACE)
- [ ] #3 A transparent/dielectric object rendered with PATH_TRACE is no longer black and refracts the scene behind it
- [ ] #4 pathShade logic (commonMain) is covered by frozen unit tests (cover-first, specs/testing.md); detekt and the full build stay green; the example scene is verified manually (expect noise; the book uses 256 samples/pixel)
<!-- AC:END -->
