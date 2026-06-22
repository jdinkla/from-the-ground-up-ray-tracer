---
id: TASK-20
title: Path tracing / global illumination tracer
status: To Do
assignee: []
created_date: '2026-06-22 09:41'
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
