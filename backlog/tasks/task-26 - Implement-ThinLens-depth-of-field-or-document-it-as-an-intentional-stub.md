---
id: TASK-26
title: Implement ThinLens depth-of-field (or document it as an intentional stub)
status: Done
assignee:
  - '@claude'
created_date: '2026-06-22 14:20'
updated_date: '2026-06-22 19:39'
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
- [x] #1 ThinLens either produces depth-of-field rays per the book's model, or is clearly documented as a non-functional stub
- [x] #2 If implemented: a scene/test demonstrates focal-plane focus with blur outside the focal plane
- [x] #3 If documented as a stub: code KDoc and any user-facing help reflect that it does not blur
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

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
Implemented the book's thin-lens DoF (Suffern ch.10) in ThinLens.kt. Added lensRadius field; sampler retyped to a new UnitDiskSampler fun-interface seam (Sampler now implements it; area lights/DiskLight unaffected). getRaySampled: view-plane point (px,py) as Pinhole, lens-disk sample scaled by lensRadius -> origin = eye + lx*u + ly*v, direction = pm(px*f/d - lx, py*f/d - ly, f).normalize() -> all aperture samples converge on the focal point at distance f. getRaySingle uses lens centre (sharp pinhole-equivalent). Rewrote KDoc (stub note removed). Added WorldScope.thinLensCamera(...) DSL (additive; default jittered disk sampler). Replaced the stub-characterizing ThinLensTest with DoF geometry tests (deterministic QueuedDiskSampler fake; hand-derived: pixel (0,4) on a 4x2 view plane, d=1,f=4,lensRadius=2 -> focal point (8,-4,-4); origins (1,0,0)/(0,1,0)/(-0.5,-0.5,0) all reach it; off-focal-plane depth -2f spreads). Added Vector3D shouldBeApprox matcher to Fixture.kt. Updated World58.kt to actually use thinLensCamera with its pre-built (but previously unused) samp1 disk sampler. AC#1+#2 met via implementation+unit test; AC#3 n/a (implemented, not stubbed).

Verified: just test (./gradlew clean check) green — detekt clean, all tests pass. Manual verification of coverage-excluded example: rendered World58.kt at 480p via ./gradlew run --args='--world=World58.kt --resolution=480p' -> valid 853x480 PNG, renders sharp (single-ray centre path), no error. FINDING for separate follow-up: SampledSingleRayRenderer is never wired into the render pipeline — Context.adapt() always constructs SimpleSingleRayRenderer (1 ray/pixel via getRaySingle), and SampledSingleRayRenderer has zero references outside its own file. So multi-sample anti-aliasing AND thin-lens DoF *visible blur* cannot render through the current pipeline; the DoF model is proven correct by the unit test instead (AC#2 'scene/test'). Wiring the sampled path is a larger, separate gap — not in this task's scope.
<!-- SECTION:NOTES:END -->

## Final Summary

<!-- SECTION:FINAL_SUMMARY:BEGIN -->
Implemented Suffern ch.10 thin-lens depth of field, replacing the no-op ThinLens stub. Added a lensRadius field and a UnitDiskSampler seam (Sampler now implements it; sampleUnitDisk body unchanged). getRaySampled places the ray origin on the lens disk (eye + lx*u + ly*v, a unit-disk sample scaled by lensRadius) and aims at the focal point (px*f/d, py*f/d) at distance f — direction pm(focalX-lx, focalY-ly, f).normalize() — so lens samples for an in-focus pixel converge (sharp at f) and spread for off-focal-plane points (blur); getRaySingle uses the lens centre (lx=ly=0), which normalizes to the identical sharp Pinhole ray (correct 1-sample/no-blur fallback). AC#1 met (DoF rays per the book; KDoc rewritten, stub note removed). AC#2 met via unit test: ThinLensTest uses a deterministic QueuedDiskSampler fake (no RNG) and proves genuine focal CONVERGENCE (three spread-aperture rays for an in-focus pixel all reach the same focal point at the focal plane — reviewer hand-verified: samples (1,0),(0,1),(-0.5,-0.5) with d=1,f=4 all hit (8,-4,-4)) AND off-plane spread (rays coincide at z=-f but differ at z=-2f), with hand-derived values. Added a thinLensCamera(...) DSL block (selectable) + WorldScopeTest cases + a Vector3D shouldBeApprox Fixture matcher; rewired the World58 example to use it (renders sharp via the single-ray centre path — reviewer re-rendered, valid PNG). The old stub-characterizing ThinLensTest assertions (which pinned the broken fixed (u+v-w) pixel-independent ray) were replaced — task-mandated behavior change of a known-broken stub, not a refactor dodge. Additive: Pinhole/Camera/Context/AbstractLens/ILens unchanged; existing tests pass. detekt clean, no baseline entries. Verified via just test (clean check + detekt + jacoco) BUILD SUCCESSFUL. Committed as 68193b4. IMPORTANT FINDING (filed separately): SampledSingleRayRenderer is unwired — Context.adapt always builds SimpleSingleRayRenderer (1 ray/pixel via getRaySingle), so multi-sample anti-aliasing AND thin-lens DoF VISIBLE BLUR cannot render through the current pipeline (the DoF model is proven by the unit test per AC#2's scene/test allowance).
<!-- SECTION:FINAL_SUMMARY:END -->
