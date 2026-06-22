---
id: TASK-27
title: >-
  Fix KDTree.hit/shadowHit discarding the inner hit result (broken rendering +
  shadows)
status: In Progress
assignee:
  - '@claude'
created_date: '2026-06-22 15:49'
updated_date: '2026-06-22 15:55'
labels:
  - bug
  - acceleration
dependencies: []
priority: high
ordinal: 30000
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
KDTree.hit wraps the caller's hit record in a fresh Hit(sr) copy and discards the populated inner result, so a KDTree returns a correct hit/shadow BOOLEAN but never propagates the actual hit distance/geometric object back to the caller. Consequences: (1) KDTree-accelerated objects never register as shadow casters — Compound.inShadow seeds tmin.t = lightDistance and only accepts an occluder when the written-back tmin.t < lightDistance, but KDTree leaves tmin.t unchanged, so d < d is false; (2) primary-ray rendering through Compound.hit is likewise broken because the geometric object/distance is discarded. The only KDTree example scene (World75) is labelled 'Does not work', confirming this. The shadowHit tmin contract was documented in TASK-14 (IGeometricObject KDoc); this task makes KDTree actually honor it. NOTE: the TASK-4 and TASK-6 frozen characterization tests deliberately PIN the current discard behavior (KDTreeBuilderTest, with explicit 'must not be fixed under this coverage task' notes) — fixing the bug REQUIRES updating those tests to assert the corrected write-back behavior, which is legitimate since the production behavior is intentionally changing from broken to working. Apply cover-first: pin current behavior, fix KDTree.hit to mutate the caller's record in place (or copy the result back), update the frozen tests' assertions to the corrected behavior, and verify a KDTree scene renders and casts shadows correctly. The KDTree is the acceleration structure from the author's diploma thesis (see CLAUDE.md), so getting it working matters.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 KDTree.hit propagates the actual hit distance and geometric object back to the caller's hit record (no discarded inner result)
- [x] #2 KDTree.shadowHit writes tmin.t back per the documented IGeometricObject contract, so KDTree-accelerated objects register as shadow casters via Compound.inShadow
- [x] #3 A KDTree-accelerated scene (fix or replace World75) renders correctly and casts correct shadows, verified by rendering
- [x] #4 The TASK-4/6 KDTreeBuilderTest characterization assertions that pinned the discard behavior are updated to assert the corrected write-back behavior; full suite + detekt green
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Fix KDTree.hit: keep Hit(sr) seed (carries the t cap into node traversal), then on success write h.t/normal/geometricObject back into the caller's sr via the IHit var setters (no cast needed). 2. Verify KDTree.shadowHit now propagates: existing 'val h = Hit(tmin.t); hit(ray,h); tmin.t = h.t' works once hit() writes back; rewrite the stale 'Known divergence' KDoc on KDTree.shadowHit and update IGeometricObject.shadowHit KDoc to drop the KDTree-divergence wording. 3. Update TASK-4/6 KDTreeBuilderTest: change the KDTree-wrapper-level assertions that pinned discard to assert write-back (KDTree.hit propagates t/object/normal; KDTree.shadowHit writes back distance). Node-level and TestBuilder TASK-4 pins stay (unrelated to this fix). 4. Add new KDTree-level tests: kdtree.hit writes closest sr.t/object/normal; kdtree.shadowHit writes back distance so inShadow-style d-check identifies occluder within d and rejects beyond d. 5. AC#3: render World75 (fix its 'Does not work' state) and confirm objects+shadows; coverage-excluded -> manual verify. 6. just test green incl detekt.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
Fix landed: KDTree.hit now seeds a local Hit(sr) (carries the t cap into node traversal) and on success writes h.t/h.normal/h.geometricObject back into the caller's sr via the IHit var setters (no cast needed). shadowHit's existing tmin.t=h.t now propagates the real occluder distance. Rewrote the stale 'Known divergence' KDoc on KDTree.shadowHit; IGeometricObject.shadowHit KDoc never named KDTree (it references Grid generically) so left as-is. Updated KDTreeBuilderTest wrapper-level tests to assert write-back + inShadow d-check; node-level and TASK-4 TestBuilder pins unchanged. KDTreeBuilderTest green.

AC#3 render-verified (examples are coverage-excluded -> manual verify). Command: ./gradlew run --args="--world=World75.kt --tracer=WHITTED --renderer=SEQUENTIAL --resolution=720p". Output ../20260622155521_World75.png: the 3x3x3 phong-sphere lattice renders correctly with specular highlights, correct front-to-back occlusion, and inter-sphere shadows. Counters confirm shadow rays now traverse the KDTree (KDTree.shadowHit=276966 == Compound.inShadow=276966). Updated World75 description from 'Does not work' to an accurate one.
<!-- SECTION:NOTES:END -->
