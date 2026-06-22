---
id: TASK-14
title: Remove deprecated/unused code and clarify shadowHit semantics
status: Done
assignee:
  - '@claude'
created_date: '2026-06-22 09:12'
updated_date: '2026-06-22 15:50'
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
- [x] #1 Deprecated/unused methods removed or justified
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

Part B done (documentation/clarification): determined the shadowHit tmin contract by reading all impls. tmin.t is OUTPUT (found distance written back on hit) AND a soft INPUT cap. Primitives override only shadowHit(ray):Shadow and IGNORE the input cap - they recompute the closest forward hit and write it back via the IGeometricObject default bridge. The cap is enforced by the sole production caller Compound.inShadow: it seeds tmin.t=d (light distance) and accepts an occluder only when written-back tmin.t<d. Acceleration structures (Grid) seed internal traversal with tmin.t and write the distance back. Documented this as KDoc on IGeometricObject.shadowHit(ray,tmin). Replaced KDTree's stale @Deprecated('uses tmin as input?') question with accurate KDoc; removed the @Deprecated annotation and the now-redundant @Suppress('DEPRECATION') in KDTreeBuilderTest (assertions unchanged/frozen). All @Deprecated annotations are now gone from src/.

Part C decision: justify-and-keep all KDTree builders. Simple2Builder is LIVE (used by scene World75.kt) - task premise that 'only SpatialMedianBuilder is wired' is inaccurate. TestBuilder/Test2Builder/ObjectMedian2Builder are referenced only in KDTreeBuilderTest (+ KDoc cross-refs in TreeBuilder), but are pinned by FROZEN TASK-4/TASK-6 characterization tests that document Suffern's alternative split heuristics and a known latent bug, which prior tasks deliberately preserved ('the bug must not be fixed under this coverage task'). Removing them would discard documented book-coverage and contradict those decisions. Kept; no code change.

GENUINE INCONSISTENCY found (NEEDS-DECISION for manager): KDTree.shadowHit's write-back is broken because KDTree.hit (KDTree.kt:51) wraps the caller's record in a fresh Hit(sr) and discards the populated inner result. Consequence: a KDTree-accelerated object returns the correct boolean from shadowHit but does NOT propagate the hit distance, so Compound.inShadow (which accepts only when tmin.t<d) never registers it as a shadow caster -> KDTree objects do not cast shadows. The same discard also breaks KDTree primary-ray rendering via Compound.hit (sr2.t stays at input -> sr2.t<sr.t false -> hit rejected). Grid does NOT have this bug (its traversal mutates sr in place; GridStructuresTest pins tmin.t==0.3). This is the documented-vs-consistent gap in AC#2. I did NOT fix it: (1) the fix touches KDTree.hit and changes rendered output (broken->working) for KDTree scenes - consequential, not a doc pass; (2) prior TASK-4/TASK-6 frozen tests explicitly preserve this discard behavior and state it 'must not be fixed' under those tasks; (3) the only KDTree scene (World75) is itself labeled 'Does not work'. Recommendation: route the KDTree.hit/shadowHit write-back fix as its own behavior-change task (cover-first with a frozen test, then update the TASK-4/6 characterization tests' notes), separate from this cleanup.
<!-- SECTION:NOTES:END -->

## Final Summary

<!-- SECTION:FINAL_SUMMARY:BEGIN -->
Removed deprecated/unused code and documented the shadowHit contract (behavior-neutral cleanup + docs pass). AC#1 MET: removed the two confirmed-uncalled @Deprecated World methods (hit(): Shade, shadowHit(ray, tmin)) plus their orphaned callee Compound.hitObjects() and dead imports — reviewer independently grep-confirmed zero callers across commonMain/jvmMain/examples/commonTest/jvmTest (incl. Counter-name strings); the live World.hit(ray, sr) overload is retained. Justified-and-kept the KDTree builders rather than removing them — finding: Simple2Builder is actually live via World75 (correcting the earlier 'only SpatialMedianBuilder is wired' premise). AC#2 documented (consistency for KDTree split out, see below): documented the shadowHit tmin contract as KDoc on IGeometricObject — tmin.t is an output (found distance written back on success) plus a soft input cap enforced solely by Compound.inShadow (seeds tmin.t=lightDistance, accepts an occluder only when written-back tmin.t<lightDistance); primitives + Grid + Compound all honor this. Replaced KDTree's stale @Deprecated('uses tmin as input?') with accurate KDoc. NEEDS-DECISION raised and resolved by the user (Option A): the 'consistent across implementations' half of AC#2 cannot be honestly met without fixing a genuine KDTree bug — KDTree.hit wraps the record in a fresh Hit(sr) copy and discards the populated inner result, so KDTree returns correct booleans but never propagates hit distance/object, breaking KDTree shadows (Compound.inShadow's tmin.t<d is d<d=false) and primary-ray rendering (World75 is labelled 'Does not work'). Corroborated by the TASK-6 reviewer's independent observation. Per user decision this rendering-behavior fix was split into the new HIGH-priority TASK-27 (cover-first, updates the TASK-4/6 frozen tests that deliberately pinned the discard behavior) rather than bundled into this cleanup task. No shadowHit/KDTree.hit LOGIC changed here (reviewer confirmed byte-identical); TASK-4/6 frozen test assertions unchanged (only a redundant @Suppress(DEPRECATION) removed). Verified via just test (clean check + detekt + jacoco) BUILD SUCCESSFUL. Committed as 54edb32.
<!-- SECTION:FINAL_SUMMARY:END -->
