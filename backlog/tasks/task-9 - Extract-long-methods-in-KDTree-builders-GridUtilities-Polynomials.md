---
id: TASK-9
title: 'Extract long methods in KDTree builders, GridUtilities, Polynomials'
status: In Progress
assignee:
  - '@claude'
created_date: '2026-06-22 09:11'
updated_date: '2026-06-22 12:04'
labels:
  - refactor
dependencies: []
references:
  - docs/arc42/11_risks_and_technical_debt.md
priority: medium
ordinal: 9000
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Several methods exceed the 60-line threshold: Simple2Builder.kt (129), ObjectMedianBuilder.kt (103), GridUtilities.kt (84 and 96), Polynomials.kt (74). Extract cohesive blocks into well-named private helpers to improve readability and testability. Pairs naturally with the coverage tasks.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 No method in the listed files exceeds the 60-line threshold
- [ ] #2 Behavior unchanged; existing and new tests pass
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Confirm cover-first: KDTreeBuilderTest, GridUtilitiesTest, PolynomialsTest green against unrefactored code (DONE - all pass).
2. Simple2Builder.build (129): extract per-axis mid-plane scan into a private helper (scan one axis -> objectsL/objectsR/both count + voxelL/voxelR), and the cost-comparison axis selection into a helper. Recursion/leaf decision stays inline.
3. ObjectMedianBuilder.build (103): extract widest-axis selection, per-axis partition+voxel construction into a private helper parameterised by axis.
4. GridUtilities.tessellateFlatSphere (84) and tessellateSmoothSphere (96): extract the top-cap, bottom-cap, and middle-ring band construction into private helpers (vertex-on-sphere formula already pure).
5. Polynomials.solveQuartic (74): extract the no-absolute-term branch and the resolvent-cubic branch into private helpers; keep resubstitution inline.
6. Re-run the three test classes after each slice; all must stay green unmodified.
7. Remove eliminated detekt baseline entries (LongMethod for all 5 methods; CyclomaticComplexMethod for Simple2Builder/ObjectMedianBuilder only if they drop under 14).
8. just test (clean check incl detekt) green.
<!-- SECTION:PLAN:END -->
