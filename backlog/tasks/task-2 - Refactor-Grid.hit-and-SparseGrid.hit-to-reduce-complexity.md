---
id: TASK-2
title: Refactor Grid.hit() and SparseGrid.hit() to reduce complexity
status: In Progress
assignee:
  - '@claude'
created_date: '2026-06-22 09:10'
updated_date: '2026-06-22 10:36'
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
- [ ] #1 Grid.hit() and SparseGrid.hit() each below cyclomatic complexity 15
- [ ] #2 No method exceeds the 60-line threshold in these files
- [ ] #3 Existing rendering behavior unchanged (golden-image / existing tests still pass)
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Cover-first: run existing GridStructuresTest (green). Add characterization tests pinning under-covered hit() paths: ray-starts-inside-grid, Compound-cell geometricObject propagation, SparseGrid empty-cell skip + negative-direction. Confirm all green against UNREFACTORED code.
2. Extract cohesive private helpers shared by Grid.hit/SparseGrid.hit logic: bbox slab t-interval computation (RaySlab/t0,t1), initial cell index, per-axis stepping setup, and the per-cell update of sr from a successful inner hit. Put reusable helpers in a small internal object/file in the acceleration package so both Grid and SparseGrid use them (SparseGrid extends Grid, differs only in cell storage: Array vs Map and null-handling).
3. Rewrite Grid.hit() and SparseGrid.hit() to use the helpers so each is < CC 15 and <= 60 lines, behavior identical.
4. Remove now-obsolete detekt-baseline entries for Grid.hit/SparseGrid.hit (and ReturnCount/NestedBlockDepth/CyclomaticComplexMethod/LongMethod for hit) so detekt proves the methods are under threshold. Leave initialize() baseline entries (out of scope).
5. Run just test (clean check). All tests + detekt green. Characterization tests unchanged.
<!-- SECTION:PLAN:END -->
