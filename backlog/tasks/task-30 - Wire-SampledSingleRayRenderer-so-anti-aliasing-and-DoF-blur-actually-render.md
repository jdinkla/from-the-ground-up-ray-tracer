---
id: TASK-30
title: Wire SampledSingleRayRenderer so anti-aliasing and DoF blur actually render
status: Done
assignee:
  - '@claude'
created_date: '2026-06-22 19:39'
updated_date: '2026-06-22 21:31'
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
- [x] #1 Context (or equivalent) selects SampledSingleRayRenderer when the view plane sample count > 1, and SimpleSingleRayRenderer when it is 1
- [x] #2 Multi-sample anti-aliasing visibly works (smooth edges) and a numSamples=1 scene renders byte-identically to before
- [x] #3 Thin-lens depth-of-field blur is visible in a rendered thinLensCamera scene at high sample counts (verifying TASK-26's DoF end-to-end)
- [x] #4 Cover-first tests for the sampled-vs-single selection and the averaged output; full suite + detekt green
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

VERIFICATION (coverage-excluded render pipeline -> manual, all at 480p, PARALLEL renderer, WHITTED tracer):

AC#2 Anti-aliasing: rendered AntiAliasingDemo.kt (samples(16)) vs AntiAliasingDemoSingleSample.kt (1 sample, identical geometry). At the sphere silhouette (row 56) the single-sample render jumps luminance 97->0 in one pixel (hard aliased step); the 16-sample render has an intermediate partial-coverage pixel (96->35->0) i.e. a smooth ramp. Soft-transition pixel count ~2x higher and soft/hard ratio 3.22 vs 1.55. Visual: jagged vs smooth sphere edge. AA confirmed working.

AC#2 byte-identical numSamples=1: Context single-sample branch is the unchanged SimpleSingleRayRenderer(world.camera.lens, tracer) call; ViewPlane.numSamples is a new field defaulting to 1, so every existing scene (none call samples()) keeps numSamples==1 and takes the identical branch. git diff confirms SimpleSingleRayRenderer, all lenses, and all existing scenes are untouched. ContextTest pins: default/numSamples=1 -> SimpleSingleRayRenderer and traces the pixel exactly once.

AC#3 Depth-of-field: rendered DepthOfFieldDemo.kt (thinLensCamera f=74, lensRadius=0.6, samples(64)) vs DepthOfFieldDemoSharp.kt (same scene, no samples -> single-ray, sharp). In the sampled render the green focal-plane box stays sharp while the near (red) and far (yellow) boxes are visibly blurred with soft spread edges; the sharp control has all three boxes crisp. soft/hard edge ratio 9.26 (DoF) vs 2.19 (sharp). This exercises ThinLens.getRaySampled aperture jitter end-to-end -> verifies TASK-26's DoF, which the single-ray path could never show.

just test (./gradlew clean check, incl detekt): GREEN. New files detekt-clean, no baseline entries added (baseline unchanged in git).
<!-- SECTION:NOTES:END -->

## Final Summary

<!-- SECTION:FINAL_SUMMARY:BEGIN -->
Wired the previously-dead SampledSingleRayRenderer into the pipeline as an OPT-IN, additive feature so anti-aliasing and thin-lens depth-of-field actually render. Added ViewPlane.numSamples (default 1) + a samples(n) DSL setter on WorldScope; Context.adapt now builds SampledSingleRayRenderer (driven by the view-plane sample count + an NRooks in-pixel sampler) when numSamples > 1, and the unchanged SimpleSingleRayRenderer(world.camera.lens, tracer) otherwise. Fixed SampledSingleRayRenderer's hardcoded numSamples=1 (now constructor-driven). AC1-4 all met. AC#2 byte-identical: numSamples defaults to 1, the single-sample branch is the unchanged Simple call, and no existing scene calls samples() — reviewer confirmed via diff that SimpleSingleRayRenderer/all lenses/all existing scenes are untouched (only ViewPlane/Context/SampledSingleRayRenderer/WorldScope changed) and the full suite passes unchanged. AC#1 selection pinned by ContextTest (both branches by instance type + a CountingTracer proving exactly numSamples rays cast). AC#4 averaging pinned by SampledSingleRayRendererTest (constant->exact color, exactly N traces, per-sample-varying mean pins the /N, rejects numSamples<=0; deterministic CountingLens fake). AC#2/#3 VISIBLE effects verified by both implementer and reviewer via rendered demos: AntiAliasingDemo (16 spp) vs single-sample twin — visibly smoother sphere silhouette (75,962B vs 65,730B PNG); DepthOfFieldDemo (thinLens f=74, lensRadius 0.6, 64 spp) vs sharp control — focal-plane green box sharp while near/far boxes show bokeh blur (172,044B vs 6,809B), verifying TASK-26's DoF end-to-end (which previously could not render blur). detekt clean, no baseline entries. Verified via just test (incl. ContextTest 5/5, SampledSingleRayRendererTest 4/4, detekt) BUILD SUCCESSFUL. Committed as 2b60a61. THIRD latent bug discovered + filed (TASK-31, Medium): Sampler.sampleUnitSquare throws IndexOutOfBounds for non-square sample counts with the sqrt-based generators (MultiJittered/Jittered/Regular), and MultiJittered also when numSets > sqrt(numSamples) — reviewer independently confirmed via probe; sidestepped here with NRooks (index-safe), root-cause fix tracked in TASK-31.
<!-- SECTION:FINAL_SUMMARY:END -->
