---
id: TASK-29
title: >-
  Remaining long-tail primitives (ConcaveSphere, bowl/thick-ring, beveled
  cylinder/wedge)
status: In Progress
assignee:
  - '@claude'
created_date: '2026-06-22 17:40'
updated_date: '2026-06-22 19:41'
labels:
  - enhancement
  - book-parity
dependencies: []
priority: low
ordinal: 32000
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Follow-up to TASK-21, which delivered the AC-required set (Annulus, PartSphere, PartCylinder, PartTorus, OpenCone, SolidCone). The remaining book primitives from TASK-21's description were deferred as the optional long-tail: ConcaveSphere (inward-facing sphere for environment/skylight domes), bowl / thick-ring (thick annulus with inner+outer spherical/cylindrical walls), beveled cylinder, and beveled wedge. Each is independent. Follow the same additive pattern TASK-21 established: study the analog primitive, implement hit()/shadowHit()/boundingBox, add an ObjectsScope DSL method following the idiom, and add cover-first hit/miss(/angular-rejection)/bbox unit tests. Note from TASK-21 review: the PartAngles wedge check is inclusive and does NOT handle a wrap-around wedge (phiMin > phiMax across the 0/2π seam) — if any new part-style primitive needs wrap-around, extend PartAngles and document it.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 ConcaveSphere, bowl/thick-ring, beveled cylinder, and beveled wedge are implemented with correct hit/shadowHit and bounding box
- [ ] #2 Each is declarable from the Builder DSL (ObjectsScope) and has cover-first hit/shadowHit unit tests
- [ ] #3 Existing primitives unchanged; detekt clean with no new baseline entries
<!-- AC:END -->
