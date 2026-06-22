---
id: TASK-10
title: Replace repeated is-Compound checks with a polymorphic method
status: In Progress
assignee:
  - '@claude'
created_date: '2026-06-22 09:11'
updated_date: '2026-06-22 12:27'
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
- [x] #1 A polymorphic method on IGeometricObject replaces the repeated is-Compound checks
- [x] #2 No is-Compound type checks remain in Grid, SparseGrid, or Compound traversal
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Cover-first: run GridStructuresTest + CompoundTest against current code (green); add characterization tests to CompoundTest pinning Compound.hit result-object dispatch (plain child, nested-compound child, plain-child-ignores-inner-record) and recursive size(); verify they pass on ORIGINAL code via stash. 2. Add polymorphic methods to IGeometricObject with defaults (getResultObject->this, combineInCell->wrap in Compound, objectCount->1, promotableToSubgrid->false, childrenForRegrid->[this]); Compound overrides all; Grid overrides promotableToSubgrid->false. 3. Replace is-Compound at GridTraversal.recordHit (getResultObject), Compound.hit (getResultObject), Compound.size (objectCount), SparseGrid.insertIntoCell (combineInCell), Grid.insertIntoCell (combineInCell + promotableToSubgrid/objectCount/childrenForRegrid). 4. Grep-verify zero is-Compound remain in Grid/SparseGrid/GridTraversal/Compound. 5. just test green.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
Discovered: a plain object inside a Compound is NOT always equal to sr2.geometricObject (e.g. Instance without material does not set sr.geometricObject=this), so the original 'is Compound ? sr2.geometricObject : geoObj' must be preserved exactly. getResultObject default returns 'this' (the occupant), Compound returns sr.geometricObject — byte-identical to the original branch. Kept the dense grid's 'as? Grid' in initializeSubcells (out of scope; not an is-Compound check); removed both is Grid and is Compound from insertIntoCell via promotableToSubgrid (Grid overrides to false so a nested sub-grid never re-promotes) + childrenForRegrid (Compound yields .objects).

DONE. Added 5 polymorphic methods to IGeometricObject (defaults for plain objects, overridden by Compound): getResultObject(sr) [hit-result dispatch], combineInCell(newObject) [cell-insertion wrap/append], objectCount() [recursive leaf count], promotableToSubgrid()+childrenForRegrid() [dense-grid promotion]. Grid overrides promotableToSubgrid()->false so a nested sub-grid never re-promotes (replacing the old 'cells[index] is Grid' first-branch ordering). Removed all 5 is-Compound sites: GridTraversal.recordHit:176, Compound.hit:62-66, Compound.size:136, SparseGrid.insertIntoCell:40, Grid.insertIntoCell:144 (+ the implicit 'is Grid' there). grep confirms ZERO is-Compound in Grid/SparseGrid/GridTraversal/Compound. Cover-first: added 4 characterization tests to CompoundTest (plain child reported as itself, nested-compound child resolves to inner leaf, plain child ignores inner record, recursive size) and verified they pass against ORIGINAL code via git stash before keeping the refactor. Detekt: adding the overrides pushed Compound to 14 and Grid to 12 functions (limit 11), tripping TooManyFunctions. Resolved with @Suppress("TooManyFunctions") on both classes - the established codebase convention (already on AreaLight, Matrix, WorldScope, ObjectsScope, AffineTransformation). 'just test' (clean check incl. detekt) is GREEN.
<!-- SECTION:NOTES:END -->
