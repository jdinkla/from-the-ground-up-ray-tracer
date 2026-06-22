---
id: TASK-32
title: Glossy reflection material and the glossy spheres example (book fig. 25.1)
status: Done
assignee: []
created_date: '2026-06-22 21:34'
updated_date: '2026-06-22 21:43'
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
- [x] #1 GlossyReflector.shade and areaLightShade add an importance-sampled glossy-reflected ray on top of the Phong direct term, weighted by cr*kr (lobe and pdf cancel)
- [x] #2 GlossySpecular's hemisphere sampler is initialised (mapSamplesToHemiSphere(exp)) so sampleF no longer throws; re-init when exp changes
- [x] #3 A glossyReflector(id, cd, ka, kd, exp, ks, cs, cr, kr) function is available in the materials DSL
- [x] #4 A new auto-discovered example scene renders three glossy spheres (blue/red/silver) with blurred reflections, verified by a multi-sample render
- [x] #5 commonMain changes covered by frozen unit tests (GlossyReflector shade + MaterialsScope glossyReflector); ./gradlew build (compile + test + detekt) is green
<!-- AC:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
Implementation:
- GlossySpecular.setupSampler() maps the hemisphere sampler with the current exp (fixes the latent IndexOutOfBounds in sampleF; documented as a precondition).
- GlossyReflector rewritten: constructor (color,ka,kd) like Reflective; init + exp-setter call setupSampler so the sampler tracks exp; exp also drives the Phong highlight (super.exp); shade() and areaLightShade() now add glossyReflection() = importance-sampled reflected ray traced via world.tracer, weighted color*(n.wi)/pdf which reduces to cr*kr*incoming; guards nDotWi<=0 / pdf<=0 / no tracer -> BLACK; added equals/hashCode covering glossySpecularBrdf.
- MaterialsScope.glossyReflector(id,cd,ka,kd,exp,ks,cs,cr,kr) added (mirrors reflective()).
- Scene examples/materials/reflective/GlossySpheres.kt (auto-discovered, id GlossySpheres.kt): blue+red satin spheres (exp 70) and a polished champagne sphere (exp 900) on a bright floor, samples(64).

Tests (cover-first, commonMain):
- GlossyReflectorShadeTest: glossy contribution == cr*kr*traced with black direct term (deterministic despite random sampling because lobe & pdf cancel); no-tracer -> BLACK. Also guards the sampleF regression.
- MaterialsScopeTest: 'should handle glossyReflector' asserts props + equals.

Verification:
- ./gradlew build green (compile + all tests + detekt + jacoco).
- Rendered at 720p (WHITTED/FORK_JOIN, 5.7s) -> faithful reproduction of fig 25.1.
<!-- SECTION:NOTES:END -->

## Final Summary

<!-- SECTION:FINAL_SUMMARY:BEGIN -->
Completed the previously dead GlossyReflector so it produces real glossy (blurred) reflections, exposed it as glossyReflector() in the materials DSL, and added the auto-discovered GlossySpheres scene reproducing book fig. 25.1. Covered the commonMain changes with frozen unit tests and verified the render visually; full build green.
<!-- SECTION:FINAL_SUMMARY:END -->
