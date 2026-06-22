---
id: TASK-21
title: Remaining geometric primitives (part objects and friends)
status: In Progress
assignee:
  - '@claude'
created_date: '2026-06-22 09:41'
updated_date: '2026-06-22 17:26'
labels:
  - enhancement
  - book-parity
dependencies: []
priority: low
ordinal: 24000
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
The book includes many primitives this port lacks: Annulus, part objects (part sphere, part cylinder, part torus, part annulus with angular/extent limits), open and solid cones, ConcaveSphere, bowl/thick-ring, and further beveled objects (beveled cylinder, beveled wedge). Long-tail parity work; each primitive is independent and can be split into its own task when picked up. Follow the cover-first rule: each new primitive needs a hit()/shadowHit() test.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Annulus plus the part-sphere / part-cylinder / part-torus primitives are implemented with correct hit and bounding box
- [ ] #2 Open and solid cones are implemented
- [ ] #3 New primitives are declarable from the Builder DSL and have hit/shadowHit unit tests
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Study existing primitives (Disk/Sphere/OpenCylinder/Torus) + base contract + DSL + tests [done].
2. Add Annulus (Disk-like, inner+outer radius ring): hit/shadowHit accept only innerR^2<dist<outerR^2; bbox center+/-outerR.
3. Add PartSphere (Sphere restricted to phi azimuth + theta polar ranges in radians): after candidate t, compute hit point relative to center, phi=atan2(x,z) wrapped [0,2pi], theta=acos(y/r), reject outside [phiMin,phiMax]/[thetaMin,thetaMax]. bbox = sphere bbox.
4. Add PartCylinder (OpenCylinder restricted to phi azimuth range): y-extent as before + phi=atan2(x,z) wrapped check.
5. Add PartTorus (Torus restricted to phi azimuth range): reuse quartic; iterate roots, accept nearest positive whose hit-point phi in range; normal as Torus.
6. Add OpenCone (lateral surface only, apex at top y=h, base radius r at y=0): quadratic in cone eqn, y-extent check, normal from gradient. Helper for normal+inside-flip.
7. Add SolidCone (Compound = OpenCone + base Disk), bbox like SolidCylinder.
8. DSL: add annulus/partSphere/partCylinder/partTorus/openCone/solidCone to ObjectsScope following existing idiom.
9. Tests per primitive: hit(t+normal), miss, angular/extent rejection for part objects, bbox. Derive expected values independently.
10. just test green incl detekt; keep hit() under thresholds (extract phi/theta helpers). Note deferred optional long-tail (ConcaveSphere/bowl/beveled cylinder+wedge).
<!-- SECTION:PLAN:END -->
