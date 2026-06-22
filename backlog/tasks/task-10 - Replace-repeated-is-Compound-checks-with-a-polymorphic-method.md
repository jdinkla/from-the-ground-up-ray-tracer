---
id: TASK-10
title: Replace repeated is-Compound checks with a polymorphic method
status: Done
assignee:
  - '@claude'
created_date: '2026-06-22 09:11'
updated_date: '2026-06-22 12:31'
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

## Final Summary

<!-- SECTION:FINAL_SUMMARY:BEGIN -->
Replaced all five repeated is-Compound type checks (plus the dense grid's is-Grid check) across the grid/compound hit-traversal and cell-insertion/promotion paths with polymorphic methods on IGeometricObject — restoring open/closed. Added getResultObject(sr) (hit-result: plain object returns this, Compound returns the inner resolved object), combineInCell(newObject) (cell insertion: plain wraps in a new Compound, Compound appends), objectCount() (recursive size), promotableToSubgrid() (Grid overrides to false to stay out of the promotion path), childrenForRegrid(); plain-object defaults live on the interface, Compound overrides all five, Grid overrides promotableToSubgrid. AC#1 and AC#2 both met — grep confirms zero is-Compound/!is-Compound remain in code in Grid.kt, SparseGrid.kt, GridTraversal.kt, Compound.kt (only KDoc references). Pure refactor, behavior byte-identical — including the subtle case the reviewer independently traced: a plain child inside a Compound reports itself (this), not sr2.geometricObject (matters for Instance-without-material). Cover-first verified: existing GridStructuresTest (nested-grid re-insertion + single->Compound->Grid promotion, frozen, reflection field names intact) unmodified and green; 4 new CompoundTest characterization tests for Compound.hit result-object dispatch and recursive size() were independently confirmed by the reviewer to PASS against the ORIGINAL production code (reverted prod files, kept tests) — genuine characterization, not retrofitted. @Suppress(TooManyFunctions) on Compound/Grid per established codebase convention (AreaLight/Matrix/WorldScope/ObjectsScope/AffineTransformation precedent), no baseline entry added. Verified via just test (clean check + detekt + jacoco) BUILD SUCCESSFUL. Committed as 9e2f330.
<!-- SECTION:FINAL_SUMMARY:END -->
