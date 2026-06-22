---
id: TASK-20
title: Path tracing / global illumination tracer
status: Done
assignee:
  - '@claude'
created_date: '2026-06-22 09:41'
updated_date: '2026-06-22 17:23'
labels:
  - enhancement
  - book-parity
dependencies: []
priority: low
ordinal: 23000
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
No global-illumination tracer exists (tracers are Whitted, AreaLighting, MultipleObjects, RayCast, SingleSphere). Add a PathTrace tracer doing Monte Carlo path tracing with the existing samplers and emissive/area lights, registered in the Tracers enum + CLI choice. Optionally a GlobalTrace variant. Requires cosine-weighted diffuse BRDF sampling (sample_f) and emissive materials acting as light sources (Emissive already exists). Higher effort; independent of the other parity tasks.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 A PathTrace tracer produces a converging GI image (color bleeding, soft indirect light) on a Cornell-box-style scene
- [x] #2 PathTrace is selectable via --tracer and exposed in the Tracers enum
- [x] #3 Cosine-weighted hemisphere sample_f is implemented for the diffuse BRDF
- [x] #4 Unit tests cover BRDF sampling pdf/distribution where practical; final image verified manually
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Complete Lambertian.sampleF as cosine-weighted hemisphere sampling: inject a Sampler (mapSamplesToHemiSphere(1.0) = cos-weighted), build ONB around sr.normal, set wi, pdf = (n.wi)*INV_PI, color = f (=cd*kd*INV_PI). Keep existing f/rho contract unchanged.
2. Add pathShade(world, sr) to IMaterial with additive default returning Color.BLACK (existing materials/tracers unaffected).
3. Matte.pathShade: sampleF on diffuse BRDF, spawn reflected ray, return f * tracer.trace(reflected, depth+1) * (n.wi) / pdf. Emissive.pathShade: return le on front face (-(normal) dot ray.dir > 0) else BLACK.
4. Add PathTrace tracer in tracers/: at depth 0 average NUM_SAMPLES sample-paths per primary ray (SimpleSingleRayRenderer gives 1 primary ray/pixel, so PathTrace self-multisamples); below depth 0 single bounce; returns BLACK past maxDepth, else sr.material.pathShade. Register PATH_TRACE in Tracers enum -> exposed via Clikt --tracer choice.
5. Cornell-box example scene under examples/: emissive ceiling panel, red/green side walls, white back/floor/ceiling, two boxes/spheres. Auto-registers via classgraph.
6. Tests (commonTest): LambertianSampleFTest - wi in hemisphere (n.wi>=0), pdf = cos(theta)*INV_PI consistent, color = f; statistical avg of n.wi over deterministic sampler ~ cos-weighted expectation (2/3). MattePathShadeTest + EmissivePathShadeTest for pathShade contract.
7. just test green incl detekt; manual GI render of Cornell box, report color bleeding observed.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
Implemented PathTrace global-illumination tracer (Suffern ch. 26).

APPROACH / DESIGN
- Lambertian.sampleF: was a stub throwing UnsupportedOperationException. Completed it as cosine-weighted hemisphere sampling: builds an orthonormal basis (u,v,w=normal) and maps a cosine-weighted hemisphere sample (Sampler.mapSamplesToHemiSphere(1.0), exp=1 => cos density). Returns wi, color=f (=cd*kd/PI), pdf=(n.wi)/PI. Sampler is a per-instance non-constructor property so it stays OUT of the data-class equals/hashCode (Matte equality + MatteTest depend on Lambertian equality by reflectance only).
- IMaterial.pathShade(world, sr): added ADDITIVELY with a default returning Color.BLACK, so all other materials and the direct-lighting tracers (Whitted/AreaLighting/...) are unaffected.
- Matte.pathShade: sampleF on the diffuse BRDF, spawn reflected ray from hitPoint, return f * tracer.trace(reflected, depth+1) * (n.wi)/pdf. With cosine weighting (n.wi)/pdf=PI, so per-bounce throughput = cd*kd * incoming.
- Emissive.pathShade: returns le on the front (emitting) face (-(normal).ray.dir>0), else BLACK — same front-face test as areaLightShade.
- PathTrace tracer: returns BLACK past world.shouldStopRecursion(depth); on hit sets sr.depth/sr.ray and returns sr.material.pathShade(world, sr); on miss returns backgroundColor. Mirrors Whitted/AreaLighting depth+hit handling.

KEY DESIGN CHOICE (documented): the render pipeline wires SimpleSingleRayRenderer = ONE primary ray/pixel, no anti-aliasing. So PathTrace does its own per-pixel Monte-Carlo averaging at the PRIMARY level: trace(ray, depth=0) averages numSamples (default 100) independent paths; deeper levels trace a single bounce. This keeps convergence sampling inside the tracer and leaves the pipeline untouched. GlobalTrace variant NOT added (out of scope; PathTrace satisfies all ACs).

REGISTRATION (AC#2): added PATH_TRACE({ w -> PathTrace(w) }) to the Tracers enum. CommandLine builds --tracer .choice() from Tracers.entries (Main passes Tracers.entries), so it is automatically offered. Verified end-to-end: render logged 'Using tracer PATH_TRACE'.

SCENE: src/examples/.../examples/globalillumination/CornellBox.kt (id 'CornellBox.kt'), auto-registers via classgraph. [0,555]^3 box, red left / green right walls, white floor/ceiling/back, emissive ceiling panel (only light), tall+short white matte boxes. ka=0 on all matte (ambient irrelevant to path tracing).

TESTS (AC#4):
- LambertianSampleFTest: wi in hemisphere (n.wi>=0) incl. a tilted normal; color==f; pdf==(n.wi)/PI exactly (by construction, sample is unit so n.wi==sp.z==cos theta); statistical mean of n.wi over 20000 deterministic-population draws ~ 2/3 (cosine-weighted expectation), tolerance 0.05 with documented reason, non-flaky (fixed 1000-direction population, only draw order randomised).
- MattePathShadeTest: result==cd*kd*incoming (direction-independent invariant); recurses at depth+1; BLACK when no tracer.
- EmissivePathShadeTest: emits le on front face, BLACK from behind.
- PathTraceTest: BLACK past recursion bound; background on miss; pathShade on deeper bounce; depth-0 averaging.
- Retired the now-false 'Lambertian.sampleF is not supported' pin in BrdfUnsupportedOperationTest (the stub it pinned is exactly what AC#3 replaces; a task-mandated behavior change, noted in-file). Other unsupported-op pins unchanged. MatteTest equality unchanged and still green.

VERIFICATION
- just test (= ./gradlew clean check, incl. detekt): GREEN. New code detekt-clean (fixed two ReturnCount violations; named consts used). Only pre-existing unchecked-cast warnings in PlyReader.kt / GridStructuresTest.kt remain.
- Manual GI render (examples are coverage-excluded): ./gradlew run --args="--world=CornellBox.kt --tracer=PATH_TRACE --renderer=FORK_JOIN --resolution=720p" -> ~35-44s. Observed unmistakable global illumination: green color bleeding from the left wall and red from the right wall onto the white floor and the box faces; the emissive ceiling panel lit; soft indirect lighting filling the box; soft contact shadows under the two boxes; characteristic Monte-Carlo grain. AC#1 satisfied.
<!-- SECTION:NOTES:END -->

## Final Summary

<!-- SECTION:FINAL_SUMMARY:BEGIN -->
Implemented the PathTrace Monte Carlo global-illumination tracer (Suffern ch. 26). Completed the previously-STUBBED (throwing) Lambertian.sampleF as cosine-weighted hemisphere sampling: builds an orthonormal basis from the normal, maps a cos-density hemisphere sample, returns wi (n·wi=cosθ>=0), f=cd*kd/PI, and pdf=cosθ/PI (reviewer verified the math, no GI bias). Added an ADDITIVE IMaterial.pathShade (default Color.BLACK) overridden in Matte (indirect diffuse bounce estimator f*trace(reflected,depth+1)*(n·wi)/pdf, guarding n·wi<=0/pdf<=0/null tracer) and Emissive (front-face Le so emissive surfaces act as lights). Registered PATH_TRACE in the Tracers enum — auto-exposed via the Clikt --tracer choice (built from Tracers.entries; render logs 'Using tracer PATH_TRACE'). PathTrace averages N independent paths per pixel at depth 0 (default 100) — a documented adaptation since the pipeline wires SimpleSingleRayRenderer (1 primary ray/pixel, no AA); the averaging is fully internal to PathTrace, leaving other tracers/pipeline untouched. AC1-4 all met. AC#1: reviewer independently rendered CornellBox.kt at 480p (16.3s, 937KB PNG) confirming unmistakable GI — green bleeding from the left wall + red from the right onto the white floor/boxes, lit emissive ceiling panel, soft indirect fill, soft contact shadows, Monte-Carlo grain. Cover-first: LambertianSampleFTest (hemisphere membership incl. tilted normal, exact pdf=cosθ/PI, constant BRDF color, deterministic statistical E[cosθ]≈2/3 with documented tolerance — NOT a flaky Monte-Carlo test), MattePathShadeTest, EmissivePathShadeTest, PathTraceTest (estimator, emissive front-face contract, tracer plumbing, depth termination — all hand-written fakes, no mocks). pathShade additive: existing materials get the BLACK default, all direct-lighting tracers (Whitted/AreaLighting/etc.) never call it — all pre-existing tests pass unchanged. The ONE retired frozen pin (BrdfUnsupportedOperationTest's Lambertian.sampleF 'not supported' assertion) is exactly what AC#3 implements — task-mandated behavior change, not a dodge; the other 3 BRDF/BTDF unsupported pins + MatteTest equality are intact (sampler kept out of Lambertian data-class identity to preserve equality). detekt clean, no baseline entries. Verified via just test (clean check + detekt + jacoco) BUILD SUCCESSFUL. Committed as 95cce75. Optional follow-up (NIT): PathTrace numSamples (100) is hardcoded — a future --samples CLI knob would ease quality/speed iteration. GlobalTrace variant not added (out of scope; PathTrace satisfies all ACs).
<!-- SECTION:FINAL_SUMMARY:END -->
