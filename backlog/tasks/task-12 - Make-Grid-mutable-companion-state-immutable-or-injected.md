---
id: TASK-12
title: Make Grid mutable companion state immutable or injected
status: In Progress
assignee:
  - '@claude'
created_date: '2026-06-22 09:12'
updated_date: '2026-06-22 12:53'
labels:
  - concurrency
  - reliability
dependencies: []
references:
  - docs/arc42/11_risks_and_technical_debt.md
priority: medium
ordinal: 12000
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Grid.kt (companion ~410-414) exposes mutable companion-object properties (logInterval, factorSize, maxDepth) that can race during parallel rendering. Make them immutable constants or pass them as construction parameters so concurrent renders cannot interfere.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Grid tuning parameters are immutable or passed per-instance, not mutable global state
- [ ] #2 Parallel renderers cannot mutate shared Grid configuration
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Convert Grid.factorSize/maxDepth from mutable companion vars into immutable constructor params Grid(factorSize: Int = 500, maxDepth: Int = 0), stored as protected val; promotion check reads instance fields.
2. Thread parent tuning into nested sub-grid: Grid(factorSize, maxDepth) instead of Grid() in insertIntoCell, preserving multi-level nesting for maxDepth>1.
3. Make logInterval an immutable shared constant (internal const val logInterval = 1000) in companion so PlyReader import resolves unchanged.
4. SparseGrid : Grid() keeps no-arg construction (defaults); behaviour identical (never promotes).
5. Verify production construction sites (Acceleration.GRID, ObjectsScope) compile unchanged.
6. Migrate GridStructuresTest withGridThresholds: delete global-mutation reflection helper; construct grid under test via TunableGrid(factorSize, maxDepth) constructor passthrough. Assertions stay byte-identical.
7. Run just test (incl. detekt), confirm green; grep-confirm no mutable global tuning state remains.
<!-- SECTION:PLAN:END -->
