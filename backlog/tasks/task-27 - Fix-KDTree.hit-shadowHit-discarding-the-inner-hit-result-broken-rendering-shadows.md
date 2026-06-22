---
id: TASK-27
title: >-
  Fix KDTree.hit/shadowHit discarding the inner hit result (broken rendering +
  shadows)
status: In Progress
assignee:
  - '@claude'
created_date: '2026-06-22 15:49'
updated_date: '2026-06-22 15:52'
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
- [ ] #1 KDTree.hit propagates the actual hit distance and geometric object back to the caller's hit record (no discarded inner result)
- [ ] #2 KDTree.shadowHit writes tmin.t back per the documented IGeometricObject contract, so KDTree-accelerated objects register as shadow casters via Compound.inShadow
- [ ] #3 A KDTree-accelerated scene (fix or replace World75) renders correctly and casts correct shadows, verified by rendering
- [ ] #4 The TASK-4/6 KDTreeBuilderTest characterization assertions that pinned the discard behavior are updated to assert the corrected write-back behavior; full suite + detekt green
<!-- AC:END -->
