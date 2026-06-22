---
id: TASK-27
title: >-
  Fix KDTree.hit/shadowHit discarding the inner hit result (broken rendering +
  shadows)
status: Done
assignee:
  - '@claude'
created_date: '2026-06-22 15:49'
updated_date: '2026-06-22 16:01'
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

## Final Summary

<!-- SECTION:FINAL_SUMMARY:BEGIN -->
Fixed the KDTree (the author's diploma-thesis acceleration structure) which returned correct hit/shadow booleans but DISCARDED the actual hit data. Root cause: KDTree.hit did node.hit(ray, Hit(sr)) — passing a fresh Hit(sr) COPY to the node, which populated the copy while the caller's sr was never updated. Fix: KDTree.hit now seeds a local Hit(sr) (carrying the incoming cap), runs node.hit, and on a hit writes t/normal/geometricObject back into the caller's sr via the IHit var setters (clean interface write, no cast — reviewer confirmed IHit exposes all three as var); on a miss sr is left untouched. With hit propagating, the pre-existing KDTree.shadowHit (Hit(tmin.t) seed → hit → tmin.t = h.t) now writes the real occluder distance back, so KDTree objects register as shadow casters via Compound.inShadow's tmin.t < d gate. AC1-4 all met. Non-regression verified: ONLY KDTree.kt changed in production — Grid/SparseGrid/primitives/Compound/inShadow/Node/InnerNode/Leaf/IGeometricObject are byte-identical, full pre-existing suite green (non-KDTree scenes unaffected). Cover-first per AC#4: the three TASK-4/6 KDTreeBuilderTest characterization assertions that deliberately pinned the discard were flipped to assert the corrected write-back on identical scenarios — reviewer independently re-derived the expected values (sphere r=0.5 at x=0, ray from x=-10 → front surface x=-0.5 → t=9.5; normal Normal(-1,0,0); miss leaves MAX_VALUE); the degenerate TestBuilder/Test2Builder pins and node-level tests were left untouched. Added a new shadow-caster test (within-d occluded at d=20 / beyond-d rejected at d=5). AC#3: World75 (formerly labelled 'Does not work') now renders correctly — reviewer ran ./gradlew run --world=World75.kt --tracer=WHITTED --renderer=SEQUENTIAL --resolution=720p → valid 1280x720 ~116KB PNG of the 3x3x3 phong-sphere lattice with correct front-to-back occlusion, specular highlights, and inter-sphere shadows; render counters confirm shadow rays traverse the tree (KDTree.shadowHit == Compound.inShadow == 276966); World75's description updated from 'Does not work'. Stale KDoc from TASK-14 rewritten to describe the now-correct behavior. detekt clean, no baseline entries. Verified via just test (clean check + detekt + jacoco) BUILD SUCCESSFUL. Committed as 951910f. Minor NIT (non-blocking): the hit KDoc slightly overstates the Hit(sr) seed's cap role for inner-node trees (InnerNode.hitRec overwrites srL.t with the bbox-exit distance; the real shadow gate is Compound.inShadow's tmin.t < d).
<!-- SECTION:FINAL_SUMMARY:END -->
