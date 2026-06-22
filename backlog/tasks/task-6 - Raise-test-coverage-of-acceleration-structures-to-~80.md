---
id: TASK-6
title: Raise test coverage of acceleration structures to ~80%
status: In Progress
assignee:
  - '@claude'
created_date: '2026-06-22 09:11'
updated_date: '2026-06-22 11:22'
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
- [ ] #1 Grid and SparseGrid branch coverage materially improved with insertion, fast-path, failure, and traversal cases
- [ ] #2 Each KDTree builder has tests exercising its split heuristic and tree construction
- [ ] #3 Combined acceleration-structure coverage approaches 80%
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Establish JaCoCo baseline for the 3 acceleration packages (combined 28.4% instr / 16.7% branch; bulk of gap is kdtree/builder at 1.2% instr).
2. Extend GridStructuresTest to close remaining Grid/SparseGrid/GridTraversal branches: slab t0>t1 reject, cells[index] is Grid nested re-insertion, SparseGrid Compound + new-Compound insertion branches, negative-direction stepping on Y/Z axes, multi-cell traversal across Y/Z, single-Geometric vs Compound vs Null cell occupancy.
3. Add KDTreeBuilderTest (new file, builder package) covering each TreeBuilder: SpatialMedian/ObjectMedian/Simple2/ObjectMedian2 build a real InnerNode tree, leaf below minChildren, node-level ray hit resolves to the right Sphere (KDTree.hit only returns the boolean since it wraps Hit(sr); assert hit info at root.hit Node level). Characterize the dead/buggy TestBuilder & Test2Builder: pin that they build a degenerate InnerNode with size()==0 and miss the ray (latent .toMutableList() bug per TASK-4) — do NOT fix.
4. Add a focused test for the KDTree wrapper itself (initialize builds root; hit returns true on intersect, false on miss; small object set -> Leaf root).
5. Re-run jacocoTestReport, report before/after acceleration coverage; run full just test (clean check incl detekt).
<!-- SECTION:PLAN:END -->
