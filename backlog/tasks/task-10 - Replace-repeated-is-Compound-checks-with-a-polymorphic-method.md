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
<!-- SECTION:NOTES:END -->
