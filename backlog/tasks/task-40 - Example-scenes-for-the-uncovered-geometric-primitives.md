---
id: TASK-40
title: Example scenes for the uncovered geometric primitives
status: In Progress
assignee:
  - '@claude'
created_date: '2026-06-23 19:15'
updated_date: '2026-06-23 19:19'
labels:
  - examples
  - coverage
dependencies: []
priority: low
ordinal: 43000
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
The TASK-38 scene audit reports 14 geometric objects with zero example scenes. These primitives are already implemented (TASK-21) and already have DSL builders in ObjectsScope (annulus, beveledCylinder, beveledWedge, bowl, concaveSphere, openCone, partAnnulus, partCylinder, partSphere, partTorus, solidCone, thickRing) — they are simply never instantiated by any scene, so the audit (and a human browsing examples) sees them as uncovered. Add example scenes that exercise them so coverage reflects the real feature set and the primitives get a visual regression check. Scenes live under src/examples and are coverage-excluded, so verify by rendering, not by unit test. Pack several primitives per scene in the style of VariousObjects.kt rather than one scene per object. ConcavePartSphere and MeshTriangle have no DSL builder yet — either add a small builder for them here or note them as out of scope and leave for a follow-up.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 New example scene(s) under src/examples instantiate each of the 12 DSL-ready uncovered primitives: Annulus, BeveledCylinder, BeveledWedge, Bowl, ConcaveSphere, OpenCone, PartAnnulus, PartCylinder, PartSphere, PartTorus, SolidCone, ThickRing
- [ ] #2 Re-running ./gradlew audit shows those 12 classes are no longer in the 'Uncovered classes' list
- [ ] #3 Each new scene renders to a non-near-black image (verified manually, e.g. via just run / swing) and is not flagged in the audit's Suspect/Failed lists
- [ ] #4 ConcavePartSphere and MeshTriangle are either also covered (with a DSL builder added) or explicitly recorded as out-of-scope follow-up in the task notes
- [ ] #5 Full build stays green: ./gradlew build (compile + test + detekt) passes
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Add two example scenes under src/examples (root examples package), small World17-style scale, infinite ground plane + ambient/point lights.
2. PartObjects.kt covers the partial-sweep family: annulus, partAnnulus, partSphere (center-param builders) + partCylinder, partTorus, openCone (origin-axis -> placed via instance{translate}).
3. SolidsAndBeveled.kt covers beveledCylinder, beveledWedge, thickRing, solidCone, bowl (instance{translate}) + concaveSphere as a large enclosing sky-dome (its idiomatic use).
4. ConcavePartSphere + MeshTriangle: no DSL builder; record as out-of-scope follow-up (per AC#4) unless cheap to add.
5. Verify: ./gradlew audit -> 12 classes leave Uncovered, scenes not Suspect/Failed; render each and eyeball; ./gradlew build green.
<!-- SECTION:PLAN:END -->
