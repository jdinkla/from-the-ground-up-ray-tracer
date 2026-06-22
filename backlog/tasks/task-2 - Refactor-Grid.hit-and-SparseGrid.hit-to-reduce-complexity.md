---
id: TASK-2
title: Refactor Grid.hit() and SparseGrid.hit() to reduce complexity
status: In Progress
assignee:
  - '@claude'
created_date: '2026-06-22 09:10'
updated_date: '2026-06-22 10:41'
labels:
  - refactor
  - acceleration
  - performance
dependencies: []
references:
  - docs/arc42/11_risks_and_technical_debt.md
priority: high
ordinal: 2000
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
The hit() methods in Grid.kt (CC=33, ~197 lines) and SparseGrid.kt (CC=36, ~193 lines) are the rendering hot path and far exceed the complexity/length thresholds. High bug-risk and hard to test. Extract cohesive blocks (bbox reject, t0/t1 setup, directional stepping, cell dispatch) into small private helpers without changing behavior.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Grid.hit() and SparseGrid.hit() each below cyclomatic complexity 15
- [x] #2 No method exceeds the 60-line threshold in these files
- [x] #3 Existing rendering behavior unchanged (golden-image / existing tests still pass)
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Cover-first: run existing GridStructuresTest (green). Add characterization tests pinning under-covered hit() paths: ray-starts-inside-grid, Compound-cell geometricObject propagation, SparseGrid empty-cell skip + negative-direction. Confirm all green against UNREFACTORED code.
2. Extract cohesive private helpers shared by Grid.hit/SparseGrid.hit logic: bbox slab t-interval computation (RaySlab/t0,t1), initial cell index, per-axis stepping setup, and the per-cell update of sr from a successful inner hit. Put reusable helpers in a small internal object/file in the acceleration package so both Grid and SparseGrid use them (SparseGrid extends Grid, differs only in cell storage: Array vs Map and null-handling).
3. Rewrite Grid.hit() and SparseGrid.hit() to use the helpers so each is < CC 15 and <= 60 lines, behavior identical.
4. Remove now-obsolete detekt-baseline entries for Grid.hit/SparseGrid.hit (and ReturnCount/NestedBlockDepth/CyclomaticComplexMethod/LongMethod for hit) so detekt proves the methods are under threshold. Leave initialize() baseline entries (out of scope).
5. Run just test (clean check). All tests + detekt green. Characterization tests unchanged.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
Approach: extracted the shared 3D-DDA traversal out of Grid.hit() and SparseGrid.hit() (near-identical ~190-line bodies) into a new behaviour-preserving helper, internal object GridTraversal (src/commonMain/.../objects/acceleration/GridTraversal.kt). It holds: computeSlab() (Shirley-Morley ray/box slab t-interval, with hits=t0<=t1), initialWalk() (initial cell index + per-axis stepping setup via axisStep()), recordHit() (Compound-vs-leaf geometricObject dispatch), and traverse() (the DDA loop, decomposed into nextAxis/exitT/acceptsHit/stepOut). Both grids now reduce to: bbox reject -> computeSlab/reject -> initialWalk -> traverse, differing only in cellAt lambda (cells[] array for Grid, nullable cellsX[] map for SparseGrid) and the onTraverse Counter hook (Grid emits Grid.hit.* counters; SparseGrid emits none, matching original). null-vs-NullObject is equivalent for the hit decision (NullObject.hit returns false).

Files changed: Grid.kt (hit() now ~26 lines), SparseGrid.kt (hit() now ~24 lines), new GridTraversal.kt, detekt-baseline.xml (removed now-obsolete CyclomaticComplexMethod/LongMethod/NestedBlockDepth entries for Grid.hit and SparseGrid.hit so detekt proves they are under threshold; kept ReturnCount for the two hit() guards, consistent with every other hit() in the repo; initialize() entries untouched, out of scope). GridStructuresTest.kt: added 6 characterization tests (cover-first) pinning previously-uncovered paths: ray-starts-inside-grid, Compound-cell geometricObject propagation, SparseGrid empty-cell skip, SparseGrid bbox miss, SparseGrid negative-direction. All passed against UNREFACTORED code first, then unchanged after refactor.

Verified: just test (./gradlew clean check) green - compile + all tests + detekt. detekt no longer flags Grid.hit/SparseGrid.hit for complexity or length (baseline entries removed), objectively confirming AC1 (CC<15) and AC2 (<=60 lines). Behaviour unchanged: all existing + new tests pass; tie-breaking axis order, hit-acceptance (sr2.t < exitT), and Compound dispatch preserved exactly.
<!-- SECTION:NOTES:END -->
