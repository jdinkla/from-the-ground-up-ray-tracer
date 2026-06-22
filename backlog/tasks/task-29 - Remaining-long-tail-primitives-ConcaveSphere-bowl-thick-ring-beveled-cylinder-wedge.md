---
id: TASK-29
title: >-
  Remaining long-tail primitives (ConcaveSphere, bowl/thick-ring, beveled
  cylinder/wedge)
status: In Progress
assignee:
  - '@claude'
created_date: '2026-06-22 17:40'
updated_date: '2026-06-22 19:42'
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

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Study analogs (Sphere/Disk/Annulus/OpenCylinder/Torus/PartSphere/SolidCone/BeveledBox + Compound + ObjectsScope + tests) [done].
2. ConcaveSphere (commonMain/objects): copy Sphere quadratic; normal points INWARD n=(center-p)/radius (negate outward). hit/shadowHit/bbox + equals/hashCode.
3. ThickRing (compound): outer OpenCylinder + inner OpenCylinder + top Annulus + bottom Annulus; bbox outer radius x [y0,y1]. Suffern ThickRing.
4. Bowl (compound): outer PartSphere lower hemisphere + inner PartSphere (inward via... reuse PartSphere outward; rim Annulus). Suffern Bowl = thick hemispherical shell between inner+outer sphere. Keep book-aligned; document.
5. BeveledCylinder (beveled/): shortened OpenCylinder body + 2 shrunk cap Disks + Torus rim at top & bottom via Instance translate. Suffern BeveledCylinder.
6. BeveledWedge (beveled/): part-cylinder slice + radial side rectangles + top/bottom part-annuli + bevel torus/sphere pieces. Suffern BeveledWedge. ATTEMPT; defer cleanly if geometry intractable (report).
7. DSL ObjectsScope: add concaveSphere/thickRing/bowl/beveledCylinder(/beveledWedge) following idiom (+ imports).
8. Tests (commonTest): per primitive hit(t+normal incl INWARD for ConcaveSphere), miss, hollow/sub-surface, bbox; derived values. DSL round-trip cases in ObjectsScopeTest.
9. just test green incl detekt; keep hit() under thresholds; no baseline entries. Report deferred primitive if any.
<!-- SECTION:PLAN:END -->
