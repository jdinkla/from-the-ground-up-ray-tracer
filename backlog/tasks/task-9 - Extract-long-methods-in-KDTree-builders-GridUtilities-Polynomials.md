---
id: TASK-9
title: 'Extract long methods in KDTree builders, GridUtilities, Polynomials'
status: In Progress
assignee:
  - '@claude'
created_date: '2026-06-22 09:11'
updated_date: '2026-06-22 12:10'
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
- [x] #1 No method in the listed files exceeds the 60-line threshold
- [x] #2 Behavior unchanged; existing and new tests pass
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

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
Pure refactor: extracted long methods into well-named private helpers; behavior unchanged (frozen tests pass unmodified).

Cover-first confirmed green against unrefactored code first: KDTreeBuilderTest (TASK-6), GridUtilitiesTest (TASK-7), PolynomialsTest (TASK-5). No new tests needed — existing coverage pins all extracted branches.

Methods split (before -> after body line counts; all now <= 60):
- Simple2Builder.build: 129 -> ~42. Extracted scanAxis() (per-axis mid-plane partition + child voxels), cost()/selectBestCandidate() (the x/y/z cost comparison + tie cascade), withComponent() (axis-clamped Point3D), and a private Candidate holder. Preserved the original quirk that split/Axis used by the InnerNode are always mid.z / Axis.fromInt(depth) regardless of winning axis. Moved cost weights 3/5 to companion const (STRADDLE_WEIGHT/DUPLICATION_WEIGHT) so they stay MagicNumber-ignored (were ignored as local-var-decl RHS before); kept the baselined n*1.5 literal inline.
- ObjectMedianBuilder.build: 103 -> ~44. Extracted widestAxis(), partition() (the three near-identical X/Y/Z partition+voxel branches collapsed via ith(axis)/withComponent()), and a Partition holder. Dropped dead locals (minAxis/maxAxis/fwidth never read; size inlined). The unreachable null-voxel fallback (?: BBox()) became an exhaustive when over the Axis enum.
- GridUtilities.tessellateFlatSphere: 84 -> 3-line delegator + tessellateFlatTopCap/BottomCap/MiddleRings. Added spherePoint(j,k,h,v) for the shared unit-sphere parametrisation.
- GridUtilities.tessellateSmoothSphere: 96 -> 3-line delegator + tessellateSmoothTopCap/BottomCap/MiddleRings + smoothTriangle() (builds a SmoothTriangle with radial per-vertex normals).
- Polynomials.solveQuartic: 74 -> ~42. Extracted solveQuarticNoAbsoluteTerm() and solveQuarticResolvent() branches + nonNegativeSqrt() (preserves the original 0.0-clamp). Resubstitution stays inline.

Baseline burndown (detekt re-run green after each removal — only removed findings actually eliminated):
- Removed LongMethod entries: GridUtilities.tessellateFlatSphere, GridUtilities.tessellateSmoothSphere, ObjectMedianBuilder.build, Polynomials.solveQuartic, Simple2Builder.build.
- Removed CyclomaticComplexMethod entries: ObjectMedianBuilder.build, Simple2Builder.build (both dropped under 14 after extraction). Left SpatialMedianBuilder entries untouched (out of scope).

Verified: just test (./gradlew clean check) PASS. Did not touch dead-code TestBuilder/Test2Builder (TASK-4 latent bug left as-is).
<!-- SECTION:NOTES:END -->
