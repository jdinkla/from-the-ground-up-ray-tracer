---
id: TASK-32
title: Glossy reflection material and the glossy spheres example (book fig. 25.1)
status: In Progress
assignee: []
created_date: '2026-06-22 21:34'
updated_date: '2026-06-22 21:37'
labels: []
dependencies: []
ordinal: 35000
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
GlossyReflector (Suffern ch. 25) exists but is dead, non-functional code: it only overrides areaLightShade (so the default WHITTED tracer uses plain Phong) and that override merely returns the BRDF colour instead of tracing a glossy-reflected ray; additionally its GlossySpecular uses a bare Sampler() whose hemisphere is never mapped, so sampleF() would throw IndexOutOfBounds on first use. Complete the material so it produces real glossy (blurred) reflections, expose it in the scene DSL, and add an example scene reproducing the three glossy spheres of figure 25.1.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 GlossyReflector.shade and areaLightShade add an importance-sampled glossy-reflected ray on top of the Phong direct term, weighted by cr*kr (lobe and pdf cancel)
- [ ] #2 GlossySpecular's hemisphere sampler is initialised (mapSamplesToHemiSphere(exp)) so sampleF no longer throws; re-init when exp changes
- [ ] #3 A glossyReflector(id, cd, ka, kd, exp, ks, cs, cr, kr) function is available in the materials DSL
- [ ] #4 A new auto-discovered example scene renders three glossy spheres (blue/red/silver) with blurred reflections, verified by a multi-sample render
- [ ] #5 commonMain changes covered by frozen unit tests (GlossyReflector shade + MaterialsScope glossyReflector); ./gradlew build (compile + test + detekt) is green
<!-- AC:END -->
