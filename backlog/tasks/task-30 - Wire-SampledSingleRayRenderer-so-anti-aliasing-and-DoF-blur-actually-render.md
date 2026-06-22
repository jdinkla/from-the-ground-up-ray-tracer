---
id: TASK-30
title: Wire SampledSingleRayRenderer so anti-aliasing and DoF blur actually render
status: In Progress
assignee:
  - '@claude'
created_date: '2026-06-22 19:39'
updated_date: '2026-06-22 21:18'
labels:
  - bug
  - renderer
  - reliability
dependencies: []
priority: medium
ordinal: 33000
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Discovered during TASK-26: Context.adapt() always builds SimpleSingleRayRenderer(world.camera.lens, tracer), which casts exactly ONE ray per pixel via ILens.getRaySingle. SampledSingleRayRenderer (which casts N samples per pixel via getRaySampled, the path needed for anti-aliasing and lens/area sampling) is referenced NOWHERE outside its own file — it is dead code. Consequences: (1) multi-sample anti-aliasing does not work — a scene's ViewPlane numSamples > 1 is effectively ignored, every render is 1 sample/pixel (aliased); (2) thin-lens depth-of-field blur (TASK-26) and any other lens/aperture sampling cannot produce visible blur through the pipeline (the DoF model is correct and unit-tested, but no multi-sample render path exercises it). Wire the sampled path: have Context.adapt choose SampledSingleRayRenderer when the ViewPlane's sample count > 1 (and SimpleSingleRayRenderer otherwise), or an equivalent selection. This is a BEHAVIOR CHANGE for any existing scene with numSamples > 1 (they will start anti-aliasing), so apply cover-first: pin current single-sample behavior where it must be preserved, verify SampledSingleRayRenderer produces correct averaged output, confirm a numSamples=1 scene is byte-identical, and manually verify AA (smooth edges) + thin-lens DoF blur on a demo scene (e.g. a thinLensCamera scene at high samples shows out-of-focus blur, and World58). Check the parallel renderers (Sequential/ForkJoin/coroutine/virtual) all drive the sampled single-ray renderer correctly.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Context (or equivalent) selects SampledSingleRayRenderer when the view plane sample count > 1, and SimpleSingleRayRenderer when it is 1
- [ ] #2 Multi-sample anti-aliasing visibly works (smooth edges) and a numSamples=1 scene renders byte-identically to before
- [ ] #3 Thin-lens depth-of-field blur is visible in a rendered thinLensCamera scene at high sample counts (verifying TASK-26's DoF end-to-end)
- [ ] #4 Cover-first tests for the sampled-vs-single selection and the averaged output; full suite + detekt green
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Add ViewPlane.numSamples (var, default 1) + samples(n) DSL setter on WorldScope (sets viewPlane.numSamples). Default 1 keeps every scene byte-identical.
2. Fix SampledSingleRayRenderer: take numSamples + in-pixel Sampler via constructor (remove hardcoded numSamples=1 and 2500-sample sampler latent bug). Loop numSamples times, jitter via sampleUnitSquare, getRaySampled, average via ColorAccumulator.
3. Wire Context.adapt: numSamples>1 -> SampledSingleRayRenderer(lens, tracer, numSamples, in-pixel sampler); ==1 -> SimpleSingleRayRenderer exactly as today (byte-identical single-sample branch, the one wiring edit).
4. Cover-first tests: ContextTest (selection: 1 -> Simple, N -> Sampled w/ right numSamples), SampledSingleRayRendererTest (averaging: constant-color tracer -> that color; per-sample-varying via deterministic fake lens+tracer -> correct mean). Keep existing RendererTest unchanged.
5. just test green (incl detekt, no baseline entries).
6. Manual: AA demo scene (hard sphere edge, numSamples=16) + DoF demo (thinLensCamera high samples) -> render, observe smooth edges + blur. Confirm numSamples=1 render byte-identical.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
Implemented: ViewPlane.numSamples (var, default 1) + samples(n) DSL setter on WorldScope. Context.adapt now selects SampledSingleRayRenderer when viewPlane.numSamples>1, else SimpleSingleRayRenderer (single-sample branch unchanged -> byte-identical default scenes).

Fixed SampledSingleRayRenderer: removed hardcoded numSamples=1 and the Sampler(MultiJittered,2500,10); now takes numSamples + index-safe in-pixel Sampler(NRooks) via constructor and casts exactly numSamples jittered rays/pixel, averaging via ColorAccumulator.

LATENT BUG FOUND (root cause the dead renderer never worked): Sampler.sampleUnitSquare indexes assuming numSamples*numSets points, but the sqrt-based generators (MultiJittered/Jittered/Regular) only generate floor(sqrt(numSamples))^2*numSets points -> IndexOutOfBounds for any non-square numSamples; MultiJittered additionally throws whenever numSets>sqrt(numSamples) (its index math uses p*numSets instead of p*n*n). Proven by probe across generators x counts x sets. Only NRooks and PureRandom are index-safe for arbitrary numSamples. Chose NRooks (stratified n-rooks) as the renderer's default in-pixel sampler. Did NOT rewrite MultiJittered (out of scope); documented the constraint in the renderer KDoc.

Tests: ContextTest (selection 1->Simple, default->Simple, >1->Sampled; wired numSamples drives exact trace count; single-sample traces once). SampledSingleRayRendererTest (constant tracer averages back to colour; exactly numSamples traces; per-sample-varying mean = ((N-1)/2)/100 pins the /N; rejects non-positive). Existing RendererTest unchanged.
<!-- SECTION:NOTES:END -->
