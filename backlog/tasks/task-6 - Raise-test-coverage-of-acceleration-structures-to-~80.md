---
id: TASK-6
title: Raise test coverage of acceleration structures to ~80%
status: In Progress
assignee:
  - '@claude'
created_date: '2026-06-22 09:11'
updated_date: '2026-06-22 11:17'
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
