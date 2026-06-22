---
id: TASK-3
title: Remove Grid/SparseGrid duplication via shared traversal strategy
status: Done
assignee:
  - '@claude'
created_date: '2026-06-22 09:10'
updated_date: '2026-06-22 10:56'
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
- [x] #1 Shared traversal/initialization logic lives in one place reused by both Grid and SparseGrid
- [x] #2 Dense vs sparse cell storage is the only divergent concern
- [x] #3 All acceleration-structure tests pass
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Confirm TASK-2's GridStructuresTest characterization tests pass against current code (cover-first). 2. Pull the duplicated initialize() body (sizing heuristic, insertion scaffold, statistics) up into Grid as a template method. 3. Express the dense-vs-sparse divergence as protected open hooks: prepareInitialization, allocateCells, insertIntoCell, initializeSubcells, cellAt, count. 4. SparseGrid overrides only those hooks (map storage, no super.initialize, no subgrid promotion, no counters). 5. Keep field names cells/cellsX/depth/factorSize/maxDepth (pinned by frozen reflection tests) and the detekt baseline ids for initialize/hit intact. 6. Run just test (clean check) green.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
Refactor (behaviour-preserving), pinned by TASK-2's frozen GridStructuresTest (17 cases, unmodified, green before and after).

Approach: turned Grid.initialize() and Grid.hit() into template methods holding all the logic shared with SparseGrid (grid-resolution cube-root heuristic, clamped cell-index ranges, the triple-nested insertion loop, per-cell counts + statistics, and the GridTraversal-based 3D-DDA from TASK-2). The dense-vs-sparse divergence is now expressed solely through a small set of protected open hooks on Grid:
 - prepareInitialization(): Grid calls super.initialize() (recomputes bbox); SparseGrid only sets isInitialized=true (keeps existing bbox).
 - allocateCells(n): dense Array<NullObject> vs reset map.
 - insertIntoCell(index, obj): dense dispatch incl. subgrid promotion vs map-backed promotion-free dispatch.
 - initializeSubcells(): dense recurses into nested grids; sparse no-op (never promotes).
 - cellAt(index): cells[index] vs cellsX[index].
 - count(event): dense -> Counter.count; sparse -> no-op (folds in the four prior counter sites + the absent Grid.shadowHit counter).
SparseGrid now overrides exactly those six hooks and nothing else; its initialize()/hit()/shadowHit() overrides are gone (inherited). statistics() lives once on Grid; its 'depth==0' guard is a no-op for SparseGrid (never nested), so behaviour matches the original unconditional call.

Constraints honoured: fields cells (Grid), cellsX (SparseGrid), depth/factorSize/maxDepth (Grid) are kept as declared fields because the frozen tests read them via reflection. initialize()/hit() kept as plain 'override' (not final) so the detekt-baseline ReturnCount/CyclomaticComplexity/LongMethod ids for Grid.initialize and Grid.hit still match — baseline file untouched. Consolidated counters into one count() hook and merged statistics to stay within detekt TooManyFunctions (Grid = 11 functions, limit 11). The @Deprecated annotation on the old SparseGrid.shadowHit is dropped (override removed); runtime behaviour is identical (inherited Grid.shadowHit + no-op count = no counter, as before).

Note: the dense-vs-sparse cell storage could not be fully extracted into a standalone strategy class because the frozen tests assert the literal field names cells/cellsX on the two classes; the protected-hook seam keeps the storage local to each class as required while removing the duplicated logic.

Verified: ./gradlew test --tests GridStructuresTest green; full 'just test' (clean check = compile + all tests + detekt) green. Files: src/commonMain/.../acceleration/Grid.kt, src/commonMain/.../acceleration/SparseGrid.kt. No examples/CLI/Swing code touched.
<!-- SECTION:NOTES:END -->

## Final Summary

<!-- SECTION:FINAL_SUMMARY:BEGIN -->
Removed the remaining Grid/SparseGrid duplication (building on TASK-2's GridTraversal extraction). Grid.initialize()/hit()/shadowHit() are now template methods holding all shared logic — the cube-root resolution heuristic, clamped cell-index ranges, the triple-nested insertion loop, per-cell counters/statistics, and the GridTraversal 3D-DDA. SparseGrid overrides only six storage-driven hooks (prepareInitialization, allocateCells, insertIntoCell, initializeSubcells, cellAt, count) and inherits everything else, so dense-vs-sparse cell storage (array vs map) is the only divergent concern. The redundant @Deprecated SparseGrid.shadowHit override was dropped (now inherited, identical behavior). Behaviour preserved and pinned by TASK-2's frozen characterization tests (GridStructuresTest, unmodified, reflected field names preserved); reviewer independently confirmed each hook is behaviour-preserving hook-by-hook. Verified via just test (clean check: compile + tests + detekt + jacoco) BUILD SUCCESSFUL; detekt-baseline untouched. Committed as f00e748.
<!-- SECTION:FINAL_SUMMARY:END -->
