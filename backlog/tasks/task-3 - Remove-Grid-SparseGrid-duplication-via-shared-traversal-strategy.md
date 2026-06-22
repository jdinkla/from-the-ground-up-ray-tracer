---
id: TASK-3
title: Remove Grid/SparseGrid duplication via shared traversal strategy
status: In Progress
assignee:
  - '@claude'
created_date: '2026-06-22 09:10'
updated_date: '2026-06-22 10:53'
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

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Confirm TASK-2's GridStructuresTest characterization tests pass against current code (cover-first). 2. Pull the duplicated initialize() body (sizing heuristic, insertion scaffold, statistics) up into Grid as a template method. 3. Express the dense-vs-sparse divergence as protected open hooks: prepareInitialization, allocateCells, insertIntoCell, initializeSubcells, cellAt, count. 4. SparseGrid overrides only those hooks (map storage, no super.initialize, no subgrid promotion, no counters). 5. Keep field names cells/cellsX/depth/factorSize/maxDepth (pinned by frozen reflection tests) and the detekt baseline ids for initialize/hit intact. 6. Run just test (clean check) green.
<!-- SECTION:PLAN:END -->
