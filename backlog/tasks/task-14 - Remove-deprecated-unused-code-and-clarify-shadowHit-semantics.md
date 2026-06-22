---
id: TASK-14
title: Remove deprecated/unused code and clarify shadowHit semantics
status: In Progress
assignee:
  - '@claude'
created_date: '2026-06-22 09:12'
updated_date: '2026-06-22 15:35'
labels:
  - cleanup
dependencies: []
references:
  - docs/arc42/11_risks_and_technical_debt.md
priority: low
ordinal: 14000
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Remove dead code marked unused (World.kt:35,49) and resolve the open questions flagged in KDTree.kt:38 and SparseGrid.kt:336 about whether shadowHit uses tmin as input. Clarify or fix the shadowHit parameter semantics so the contract is unambiguous.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Deprecated/unused methods removed or justified
- [ ] #2 shadowHit parameter semantics documented and consistent across implementations
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Part A: Confirm via grep that World.hit() (no-arg) and World.shadowHit(ray,tmin) are uncalled anywhere (prod, examples, tests); remove both deprecated methods plus their Counter.count lines and now-unused imports (Shade, ShadowHit). Keep World.hit(ray,sr) and rest of World.
2. Part B: Determine the actual shadowHit tmin contract by reading all implementations. Document it precisely as KDoc on IGeometricObject.shadowHit (tmin.t = INPUT cap distance + OUTPUT hit distance written back; primitives ignore input and recompute; caller Compound.inShadow enforces the cap via t.t<d). Replace KDTree's stale @Deprecated('uses tmin as input?') with accurate KDoc stating the real contract and the known write-back divergence (KDTree.hit discards its inner Hit record, so the distance is NOT propagated, unlike Grid).
3. Part C: Decide on dead KDTree builders (TestBuilder/Test2Builder/ObjectMedian2Builder). They are already characterized by frozen TASK-4/6 tests pinning book heuristics; justify-keep.
4. Assess the genuine KDTree<->Grid inconsistency: KDTree shadowHit write-back is broken (objects in a KDTree do not cast shadows / do not render via Compound). Prior tasks (TASK-4/6) deliberately froze this discard behavior; only scene World75 uses kdtree and is labeled 'Does not work'. Fixing changes rendered output -> consequential -> NEEDS-DECISION rather than silent fix.
5. Run just test green; keep detekt clean (removing @Deprecated may clear warnings).
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
Part A done: confirmed via grep (prod commonMain/jvmMain, examples, commonTest/jvmTest) that World.hit() (no-arg, calling compound.hitObjects()) and World.shadowHit(ray,tmin) had ZERO callers (only self-references in their own bodies). Removed both + their Counter.count lines + now-unused imports (Shade, ShadowHit) in World.kt. Their sole transitive callee Compound.hitObjects() (returned an empty Shade(), only ever called by the removed World.hit()) became dead too -> removed it and the now-unused Shade import in Compound.kt. No test referenced any of the removed methods. Shade class itself stays (widely used by tracers/tests).
<!-- SECTION:NOTES:END -->
