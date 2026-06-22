---
id: TASK-2
title: Refactor Grid.hit() and SparseGrid.hit() to reduce complexity
status: In Progress
assignee:
  - '@claude'
created_date: '2026-06-22 09:10'
updated_date: '2026-06-22 10:35'
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
