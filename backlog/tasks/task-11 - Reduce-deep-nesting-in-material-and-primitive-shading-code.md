---
id: TASK-11
title: Reduce deep nesting in material and primitive shading code
status: In Progress
assignee:
  - '@claude'
created_date: '2026-06-22 09:11'
updated_date: '2026-06-22 12:43'
labels:
  - refactor
dependencies: []
references:
  - docs/arc42/11_risks_and_technical_debt.md
priority: medium
ordinal: 11000
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Eight blocks exceed the nesting depth threshold of 4, notably Matte.kt areaLightShade (66), Phong.kt areaLightShade (77), and OpenCylinder.kt hit (29). Apply early returns and extract nested blocks into helper methods to flatten control flow.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 No block exceeds nesting depth 4 in the affected methods
- [x] #2 Shading/intersection behavior unchanged; tests pass
<!-- AC:END -->



## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Run detekt without baseline: actual NestedBlockDepth findings = ONLY Matte.areaLightShade (depth 5). Phong (uses filterIsInstance, depth 4), OpenCylinder.hit (depth 4) already at threshold; Grid/SparseGrid baseline entries are stale. Task description (8 blocks) predates code drift.
2. COVER-FIRST: MatteTest does not exercise areaLightShade. Add MatteAreaLightShadeTest (4 cases: normal contribution, all-shadowed, non-area-light ignored, samples-facing-away) + Color shouldBeApprox matcher in Fixture.kt. Confirm green on unrefactored code.
3. Flatten Matte.areaLightShade with filterIsInstance + extracted private helper (sampleContribution) and guard clauses to depth <= 4. Behavior-preserving.
4. Remove only the Matte NestedBlockDepth baseline entry. Run just test (incl detekt) green.
5. Report stale Grid/SparseGrid baseline entries as a NEEDS-DECISION note (out of scope to remove).
<!-- SECTION:PLAN:END -->
