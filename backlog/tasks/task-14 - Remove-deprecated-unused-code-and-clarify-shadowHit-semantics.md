---
id: TASK-14
title: Remove deprecated/unused code and clarify shadowHit semantics
status: In Progress
assignee:
  - '@claude'
created_date: '2026-06-22 09:12'
updated_date: '2026-06-22 15:28'
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
