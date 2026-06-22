---
id: TASK-12
title: Make Grid mutable companion state immutable or injected
status: In Progress
assignee:
  - '@claude'
created_date: '2026-06-22 09:12'
updated_date: '2026-06-22 12:54'
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
- [x] #1 Grid tuning parameters are immutable or passed per-instance, not mutable global state
- [x] #2 Parallel renderers cannot mutate shared Grid configuration
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

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
Approach: converted Grid's mutable companion vars factorSize/maxDepth into immutable per-instance constructor params 'open class Grid(protected val factorSize: Int = 500, protected val maxDepth: Int = 0)'. Defaults preserved via private const DEFAULT_FACTOR_SIZE=500 / DEFAULT_MAX_DEPTH=0. logInterval made an immutable shared constant (internal const val logInterval = 1000) in the companion, so PlyReader's 'import ...Grid.Companion.logInterval' resolves unchanged. No mutable global tuning state remains on Grid (companion now holds only const vals).

Sub-grid threading: promotion in insertIntoCell now builds the nested grid as Grid(factorSize, maxDepth) instead of Grid(), so each level inherits the parent's tuning and multi-level nesting (maxDepth>1) behaves exactly as the old shared-global code did. depth is still set post-construction as before.

SparseGrid: unchanged 'class SparseGrid : Grid()' — the all-default constructor keeps the no-arg super call valid; SparseGrid never promotes (promotableToSubgrid=false), so tuning is irrelevant to it and behaviour is identical. Production construction sites Acceleration.GRID ({ Grid() }) and ObjectsScope.kt (val compound = Grid()) compile and behave unchanged (default tuning).

Test change (arrange-only): GridStructuresTest's TunableGrid now takes (factorSize, maxDepth) and passes them to Grid(...). The two promotion tests construct TunableGrid(factorSize=0, maxDepth=1) and TunableGrid(factorSize=0, maxDepth=2) instead of wrapping init in the global-mutation helper. Deleted the now-defunct withGridThresholds reflection helper (the static fields it mutated no longer exist). All assertions are byte-identical (cells.size, nested is Grid, depth==1, nested.objects.size==4, ray-hit t==0.15, etc.) — only the injection mechanism changed. The unrelated cells/cellsX reflection was left as-is.

Verified: ./gradlew test --tests GridStructuresTest green; 'just test' (clean check incl. detekt) BUILD SUCCESSFUL, no new detekt findings (only two pre-existing unchecked-cast warnings). grep confirms no remaining mutable global Grid tuning; parallel renderers (jvmMain/renderer) never referenced it and there is nothing mutable left to reach.
<!-- SECTION:NOTES:END -->
