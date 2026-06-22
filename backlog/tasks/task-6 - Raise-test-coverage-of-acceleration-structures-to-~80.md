---
id: TASK-6
title: Raise test coverage of acceleration structures to ~80%
status: Done
assignee:
  - '@claude'
created_date: '2026-06-22 09:11'
updated_date: '2026-06-22 11:34'
labels:
  - testing
  - acceleration
dependencies: []
references:
  - docs/arc42/11_risks_and_technical_debt.md
  - TECH_DEBT.md
priority: high
ordinal: 6000
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Acceleration structures are critical-path but largely untested (overall ~51% instr / 38% branch). Grid.kt (388 missed) and SparseGrid.kt (332 missed) need already-initialized/empty fast paths, cell insertion branches (Grid vs Compound vs Null), invalid cell-division failure path, and hit traversal for bbox reject, t0>t1, and positive/negative direction stepping. KDTree builders are 400-750 missed instructions each (ObjectMedian2/Test2/Test/Simple2/ObjectMedian).
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Grid and SparseGrid branch coverage materially improved with insertion, fast-path, failure, and traversal cases
- [x] #2 Each KDTree builder has tests exercising its split heuristic and tree construction
- [x] #3 Combined acceleration-structure coverage approaches 80%
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Establish JaCoCo baseline for the 3 acceleration packages (combined 28.4% instr / 16.7% branch; bulk of gap is kdtree/builder at 1.2% instr).
2. Extend GridStructuresTest to close remaining Grid/SparseGrid/GridTraversal branches: slab t0>t1 reject, cells[index] is Grid nested re-insertion, SparseGrid Compound + new-Compound insertion branches, negative-direction stepping on Y/Z axes, multi-cell traversal across Y/Z, single-Geometric vs Compound vs Null cell occupancy.
3. Add KDTreeBuilderTest (new file, builder package) covering each TreeBuilder: SpatialMedian/ObjectMedian/Simple2/ObjectMedian2 build a real InnerNode tree, leaf below minChildren, node-level ray hit resolves to the right Sphere (KDTree.hit only returns the boolean since it wraps Hit(sr); assert hit info at root.hit Node level). Characterize the dead/buggy TestBuilder & Test2Builder: pin that they build a degenerate InnerNode with size()==0 and miss the ray (latent .toMutableList() bug per TASK-4) — do NOT fix.
4. Add a focused test for the KDTree wrapper itself (initialize builds root; hit returns true on intersect, false on miss; small object set -> Leaf root).
5. Re-run jacocoTestReport, report before/after acceleration coverage; run full just test (clean check incl detekt).
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
Additive testing only; no production behavior changed.

Coverage (combined across the 3 acceleration packages, JaCoCo):
- BEFORE: 28.4% instruction / 16.7% branch / 30.2% line
- AFTER:  80.7% instruction / 66.2% branch / 88.0% line
Per package after: acceleration 94.0% instr / 88.0% branch; kdtree/builder 77.1% / 59.0%; kdtree 64.9% / 58.3%.

Files:
- NEW src/commonTest/.../objects/acceleration/kdtree/builder/KDTreeBuilderTest.kt: covers each TreeBuilder. SpatialMedian/ObjectMedian/Simple2/ObjectMedian2 build a real InnerNode and a node-level ray hit resolves to a Sphere (asserts t, normal, object). Leaf path below minChildren. ObjectMedian X/Y/Z widest-axis branches; SpatialMedian x/y/z depth-cycle + right-of-split InnerNode descent. TestBuilder & Test2Builder pinned as CHARACTERIZATION of the dead-code latent bug from TASK-4 (calcSplit copies out-lists via .toMutableList(), so the tree is a degenerate InnerNode with size()==0 that hits nothing) - NOT fixed, comments flag it.
- EXTENDED GridStructuresTest.kt (built on TASK-2/3): nested-grid re-insertion (cells[index] is Grid branch), SparseGrid second/third-occupant Compound branches, y-axis (positive) and z-axis (negative) multi-cell DDA stepping via spacer objects, empty-leading-cell skip, Grid.toString.

Key behaviors discovered/relied on (documented in test comments):
- KDTree.hit wraps Hit(sr) and never copies the result back, so only its boolean is observable; geometric assertions are made at the Node level (tree.root!!.hit). Same reason KDTree.shadowHit cannot propagate tmin.t (only boolean asserted).
- InnerNode.hit returns the FIRST child hit, not a globally nearest one (Simple2 test pins t=13.5 to the x=4 sphere, not over-asserting 'nearest').

Not reached / why: Grid/GridTraversal slab 't0>t1' reject branch (Grid.kt ~227) is effectively unreachable independently because Grid.hit first calls boundingBox.isHit which computes the identical t0/t1 (stricter), so any ray reaching computeSlab already has t0<=t1. InnerNode.Side enum + the large commented-out hitXX/hitX methods are dead code. ObjectMedian2 isFound-retry loop and some buggy-builder deep branches remain partially uncovered; combined still clears ~80%.

Verification: just test (./gradlew clean check incl. detekt) BUILD SUCCESSFUL. Two unchecked-cast compiler warnings in GridStructuresTest reuse the pre-existing reflection idiom already in that file; not detekt failures.
<!-- SECTION:NOTES:END -->

## Final Summary

<!-- SECTION:FINAL_SUMMARY:BEGIN -->
Raised acceleration-structure test coverage via additive tests only (no production change). Extended GridStructuresTest.kt to close remaining Grid/SparseGrid/GridTraversal branches (nested-grid re-insertion via cells[index] is Grid, sparse second/third-occupant Compound insertion, +y/-z multi-cell DDA stepping, empty-leading-cell skip, toString) and added KDTreeBuilderTest.kt exercising every TreeBuilder's split heuristic + tree construction with node-level geometric hit assertions. SpatialMedian/ObjectMedian/Simple2/ObjectMedian2 tested as working builders; the dead TestBuilder/Test2Builder are pinned as degenerate (size()==0, hits nothing) — characterizing the TASK-4 latent .toMutableList()-copy bug, not fixing it. Reviewer independently re-ran jacoco: combined acceleration coverage 28.4% -> 80.7% instruction / 16.7% -> 66.2% branch / 30.2% -> 88.0% line (AC#3 met). Geometric results asserted at Node level because KDTree.hit only returns a boolean publicly (wraps Hit(sr), never copies back). The Grid slab t0>t1 reject branch is genuinely unreachable (boundingBox.isHit applies a stricter interval first) — documented, not forced. Determinism/testing.md compliant (shouldBeApprox for computed doubles, stub fakes, no RNG/time). Verified via just test (clean check + detekt + jacoco) BUILD SUCCESSFUL. Committed as db7cd50. Correction to TASK-4 note: only TestBuilder/Test2Builder carry the .toMutableList() bug; ObjectMedian2Builder does NOT (it passes out-lists directly and builds a working tree).
<!-- SECTION:FINAL_SUMMARY:END -->
