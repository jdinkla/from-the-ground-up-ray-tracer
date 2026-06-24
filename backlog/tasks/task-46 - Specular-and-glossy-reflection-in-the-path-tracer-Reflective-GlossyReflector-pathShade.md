---
id: TASK-46
title: >-
  Specular and glossy reflection in the path tracer (Reflective/GlossyReflector
  pathShade)
status: To Do
assignee: []
created_date: '2026-06-24 08:23'
labels:
  - book-coverage
  - global-illumination
  - materials
  - chapter-26
dependencies: []
references:
  - Chapter 26 Global Illumination _ Ray Tracing from the Ground Up.pdf
priority: medium
ordinal: 49000
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
The PATH_TRACE tracer (Suffern ch. 26) currently produces global illumination only for diffuse and emissive surfaces: only Matte and Emissive override IMaterial.pathShade, while Reflective, GlossyReflector, Transparent and Dielectric fall back to the default that returns BLACK. So a mirror or glossy-reflective surface renders black under PATH_TRACE, and the path tracer cannot produce specular-to-diffuse light transport or the caustics in book Figures 26.8 (flat mirror) and 26.9 (cardioid caustic from a concave cylindrical reflector). The reflectance sampling already exists (PerfectSpecular.sampleF, GlossySpecular.sampleF), so this is a small material-level addition that mirrors Matte.pathShade (recurse via world.tracer.trace(ray, depth+1)). See book Listing 26.5 (Reflective path_shade) and exercise 26.9 (GlossyReflector).
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Reflective.pathShade samples the perfect-specular direction (PerfectSpecular.sampleF), traces the reflected ray one level deeper via world.tracer.trace(ray, depth+1) and returns the weighted incoming radiance, matching book Listing 26.5
- [ ] #2 GlossyReflector.pathShade does the equivalent using GlossySpecular.sampleF (book exercise 26.9)
- [ ] #3 A reflective object rendered with the PATH_TRACE tracer is no longer black; rendering a GI scene (e.g. CornellBox variant) with a mirror shows reflections
- [ ] #4 A new auto-discovered example scene demonstrates a reflective caustic: matte plane + emissive sphere + flat mirror (book Figure 26.8) with preferredTracer(PATH_TRACE); optionally a concave cylindrical reflector for the cardioid caustic (Figure 26.9)
- [ ] #5 The two pathShade overrides (commonMain) are covered by frozen unit tests per the cover-first rule and specs/testing.md; detekt and the full build stay green; the example scene is verified manually by rendering
<!-- AC:END -->
