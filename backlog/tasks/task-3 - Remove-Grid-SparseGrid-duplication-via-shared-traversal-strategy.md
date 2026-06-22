---
id: TASK-3
title: Remove Grid/SparseGrid duplication via shared traversal strategy
status: To Do
assignee: []
created_date: '2026-06-22 09:10'
labels:
  - refactor
  - acceleration
dependencies:
  - TASK-2
references:
  - docs/arc42/11_risks_and_technical_debt.md
priority: high
ordinal: 3000
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Grid.kt and SparseGrid.kt share nearly identical hit() and initialize() logic, so every bug fix must be applied twice. Extract the common traversal and initialization into a shared base class or strategy, keeping only the cell-storage difference (dense vs sparse) specialized.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Shared traversal/initialization logic lives in one place reused by both Grid and SparseGrid
- [ ] #2 Dense vs sparse cell storage is the only divergent concern
- [ ] #3 All acceleration-structure tests pass
<!-- AC:END -->
