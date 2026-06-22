---
id: TASK-20
title: Path tracing / global illumination tracer
status: In Progress
assignee:
  - '@claude'
created_date: '2026-06-22 09:41'
updated_date: '2026-06-22 17:08'
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
- [ ] #1 A PathTrace tracer produces a converging GI image (color bleeding, soft indirect light) on a Cornell-box-style scene
- [ ] #2 PathTrace is selectable via --tracer and exposed in the Tracers enum
- [ ] #3 Cosine-weighted hemisphere sample_f is implemented for the diffuse BRDF
- [ ] #4 Unit tests cover BRDF sampling pdf/distribution where practical; final image verified manually
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
