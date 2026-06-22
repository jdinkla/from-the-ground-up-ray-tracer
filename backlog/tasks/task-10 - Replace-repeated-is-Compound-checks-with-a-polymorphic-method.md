---
id: TASK-10
title: Replace repeated is-Compound checks with a polymorphic method
status: To Do
assignee: []
created_date: '2026-06-22 09:11'
labels:
  - refactor
  - design
dependencies: []
references:
  - docs/arc42/11_risks_and_technical_debt.md
priority: medium
ordinal: 10000
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Grid.kt (341,360,378), SparseGrid.kt (139,158,177) and Compound.kt (62,65) repeat `is Compound` type checks, violating open/closed. Introduce a polymorphic method (e.g. getResultObject()) on IGeometricObject so each type decides its own behavior, removing the instanceof branching.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 A polymorphic method on IGeometricObject replaces the repeated is-Compound checks
- [ ] #2 No is-Compound type checks remain in Grid, SparseGrid, or Compound traversal
<!-- AC:END -->
