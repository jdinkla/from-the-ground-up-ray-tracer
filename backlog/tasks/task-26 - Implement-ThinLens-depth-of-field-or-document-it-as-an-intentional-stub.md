---
id: TASK-26
title: Implement ThinLens depth-of-field (or document it as an intentional stub)
status: In Progress
assignee:
  - '@claude'
created_date: '2026-06-22 14:20'
updated_date: '2026-06-22 19:28'
labels:
  - bug
  - camera
dependencies: []
priority: low
ordinal: 29000
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
ThinLens.getDirection ignores the pixel coordinates, the lens sample point, focal distance f, lens radius d, and the sampler, always returning a fixed pm(1,1,1) ray -- it does not implement the book's thin-lens depth-of-field model (Suffern ch. 10). Either implement proper thin-lens DoF (sample the lens disk, compute the focal-plane hit, build the ray from the lens point to the focal point) or, if DoF is intentionally out of scope, document ThinLens as a stub and curtail its CLI/scene exposure so it does not silently produce a pinhole-like image. Discovered while working TASK-13.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 ThinLens either produces depth-of-field rays per the book's model, or is clearly documented as a non-functional stub
- [ ] #2 If implemented: a scene/test demonstrates focal-plane focus with blur outside the focal plane
- [ ] #3 If documented as a stub: code KDoc and any user-facing help reflect that it does not blur
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Add UnitDiskSampler seam: tiny interface { sampleUnitDisk(): Point2D }; make Sampler implement it (additive). Lets ThinLens take an injectable disk sampler so the DoF test is deterministic (fake over real RNG-backed Sampler).
2. Implement ThinLens DoF per Suffern ch.10: add lensRadius field; type sampler as UnitDiskSampler?. getRaySampled(r,c,sp): view-plane point px,py (as Pinhole, incl. sp); lens-disk sample (lx,ly)=lensRadius*sampler.sampleUnitDisk(); focal point at f -> (px*f/d, py*f/d); origin=eye+lx*u+ly*v; dir=((px*f/d - lx)*u + (py*f/d - ly)*v - f*w).normalize(). getRaySingle uses lens center (lx=ly=0) -> sharp ray toward (px*f/d, py*f/d, -f). Rewrite KDoc (remove stub note, document DoF + params).
3. Replace stub-characterizing ThinLensTest with DoF tests: deterministic fake UnitDiskSampler; assert (a) getRaySingle == pinhole-equivalent sharp ray (hand-derived); (b) multiple getRaySampled with different disk points -> origins spread across lens disk (each within lensRadius of eye in u/v plane, =hand-derived values) yet all converge at the SAME focal point at distance f (ray.linear(t) at focal plane equal); (c) an off-focal-plane target gets a spread (blur) across the lens samples; (d) directions normalized. This replaces the old stub-pinning tests = an intentional behavior change for a known-broken stub (documented loudly).
4. Add WorldScope.thinLensCamera(...) DSL (mirrors camera(...)): builds Camera whose lens is a configured ThinLens (eye/lookAt/up via Camera; d,f,lensRadius,sampler set on lens). Add DSL test asserting the world's lens is a ThinLens with the configured params.
5. Optionally add an example scene using thinLensCamera (renders sharp via single-ray path; note that). Update World58 to actually use thinLensCamera with samp1 (it currently builds samp1 but never uses it).
6. just test green; detekt-clean (named constants, no baseline). REPORT: SampledSingleRayRenderer is unwired in Context.adapt -> DoF/AA blur cannot render through current pipeline (separate follow-up).
<!-- SECTION:PLAN:END -->
