---
id: TASK-21
title: Remaining geometric primitives (part objects and friends)
status: In Progress
assignee:
  - '@claude'
created_date: '2026-06-22 09:41'
updated_date: '2026-06-22 17:35'
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
- [x] #1 Annulus plus the part-sphere / part-cylinder / part-torus primitives are implemented with correct hit and bounding box
- [x] #2 Open and solid cones are implemented
- [x] #3 New primitives are declarable from the Builder DSL and have hit/shadowHit unit tests
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

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
Implemented 6 AC-required primitives (additive only; no existing primitive touched).

PRIMITIVES (commonMain/objects):
- Annulus.kt: Disk with inner+outer radius; hit/shadowHit accept only innerR^2<=|p-c|^2<=outerR^2 in the plane. bbox center+/-outerR.
- PartSphere.kt: Sphere restricted to azimuth phi in [phiMin,phiMax] and polar theta in [thetaMin,thetaMax] (radians). After each quadratic root, hit point converted via PartAngles (phi=atan2(localX,localZ) wrapped [0,2pi); theta=acos(localY/r)) and rejected outside wedge. bbox = full sphere bbox.
- PartCylinder.kt: OpenCylinder restricted to phi wedge in addition to y-extent; same outward-normal inside-flip as OpenCylinder.
- PartTorus.kt: reuses Torus quartic (Polynomials.solveQuartic) + bbox cull; iterates roots, keeps nearest forward root whose hit-point phi in wedge; Torus computeNormal.
- OpenCone.kt: lateral surface only, axis +y, base radius r at y=0, apex (0,h,0). Surface x^2+z^2=(r/h)^2*(h-y)^2 -> quadratic A=dx^2+dz^2-k*dy^2, B=2(ox*dx+oz*dz)+2k*g*dy, C=ox^2+oz^2-k*g^2 with g=h-oy,k=(r/h)^2. Normal=(x,k(h-y),z) normalized, inside-flip. y-extent [0,h].
- compound/SolidCone.kt: Compound = OpenCone lateral + base Disk facing -y at y=0; bbox-gated super.hit like SolidCylinder.
- PartAngles.kt: shared phi/theta helpers for the part objects.

DSL (ObjectsScope.kt): added annulus(), openCone(), partCylinder(), partSphere(), partTorus(), solidCone() following existing idiom (params + material id + .add(material)); scope already @Suppress(TooManyFunctions).

TESTS: per-primitive hit (t+normal), miss, and angular/extent rejection (ray that hits the FULL object but is correctly rejected because outside the phi/theta/y limits) + bbox. Independently derived expected values (e.g. Annulus t=2 normal UP; PartSphere hit (1,0,0) t=1 normal RIGHT, phi-reject ray (-0.5,0,-2)+z, theta-reject ray (0,-0.5,-2)+z; PartCylinder t=1 normal RIGHT, phi-reject (-0.5,0,2)-z, y-extent-reject y=2; PartTorus t=1.5 inner wall, phi-reject -x; OpenCone hit (0.5,1,0) t=1.5 normal x>0/y>0/z=0, y-extent-reject y=3, wide-miss; SolidCone lateral t=1.5 + base-cap t=2 normal DOWN). 6 new DSL tests in ObjectsScopeTest (equals/hashCode added to each primitive).

VERIFICATION: just test (./gradlew clean check incl detekt) GREEN. New code detekt-clean, no baseline entries (restructured hit/shadowHit via roots()/isValid()/accept() helpers to satisfy ReturnCount<=2 and ComplexCondition<=3; named TWO/FOUR/MAX_ROOTS consts for MagicNumber). Pre-existing warnings (PlyReader unchecked cast, GridStructuresTest) unrelated.

DEFERRED (out of scope per task): optional long-tail primitives mentioned in the description but NOT in the ACs - ConcaveSphere, bowl/thick-ring, beveled cylinder, beveled wedge. Recommend a follow-up task. No example scene added (nice-to-have, not AC-required; unit tests are the primary verification).
<!-- SECTION:NOTES:END -->
