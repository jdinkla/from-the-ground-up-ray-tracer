---
id: TASK-29
title: >-
  Remaining long-tail primitives (ConcaveSphere, bowl/thick-ring, beveled
  cylinder/wedge)
status: In Progress
assignee:
  - '@claude'
created_date: '2026-06-22 17:40'
updated_date: '2026-06-22 19:55'
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
- [x] #2 Each is declarable from the Builder DSL (ObjectsScope) and has cover-first hit/shadowHit unit tests
- [x] #3 Existing primitives unchanged; detekt clean with no new baseline entries
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

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
DEFERRAL DECISION (BeveledCylinder + BeveledWedge): the book's beveled cylinder/wedge round their rims with tori. This port's existing Torus.hit (Polynomials.solveQuartic) returns SPURIOUS phantom roots for near-vertical rays: a bare Torus(0.9,0.1) queried with a downward ray from y=3 reports hits at y~0.89/0.66/0.03 even though the tube only spans y in [-0.1,0.1]. This is a pre-existing Torus numerical defect (TASK-23 fixed one solveQuartic bug but not this conditioning), confirmed by direct probe of the untranslated primitive. A torus-based BeveledCylinder/BeveledWedge therefore produces wrong intersections (visible phantom specks under overhead/top-down rays) — exactly the 'wrong intersection' the task says to avoid. Per scope discipline (no fixing existing Torus; additive only) I DEFERRED both beveled primitives rather than ship known-wrong geometry. Delivered the 3 robust, torus-free primitives instead: ConcaveSphere, ThickRing, Bowl (+ supporting ConcavePartSphere used by Bowl). Recommend a follow-up task: fix Torus/solveQuartic conditioning for axis-parallel rays first, THEN add BeveledCylinder (shortened OpenCylinder + 2 shrunk Disk caps + Torus rim at each end via Instance) and BeveledWedge (PartCylinder walls + PartAnnulus caps + Rectangle radial sides + PartTorus rim bevels) — the constructions were drafted and validated structurally; only the torus solver blocks them.

IMPLEMENTED (additive only; no existing primitive modified):
PRIMITIVES (commonMain):
- objects/ConcaveSphere.kt: same quadratic as Sphere but the surface normal is negated to point INWARD (toward centre) for environment/skylight domes. hit/shadowHit/bbox + equals/hashCode/toString. roots()/accept() helpers keep hit() under detekt thresholds.
- objects/ConcavePartSphere.kt: PartSphere with inward normal (concave counterpart); reuses PartAngles phi/theta wedge rejection. Used as the inner wall of Bowl.
- objects/compound/ThickRing.kt (Compound): outer OpenCylinder + inner OpenCylinder + top Annulus + bottom Annulus; bbox outerRadius x [y0,y1]; bbox-gated super.hit like SolidCylinder. Suffern ThickRing.
- objects/compound/Bowl.kt (Compound): thick hemispherical shell opening upward = outer PartSphere lower-hemisphere (convex) + inner ConcavePartSphere lower-hemisphere (concave) + rim Annulus at y=0. theta band [PI/2,PI]. bbox lower half only. Suffern Bowl.
DSL (ObjectsScope.kt): concaveSphere(), thickRing(), bowl() following the existing idiom (params + material id + .add(material)).
TESTS (cover-first, Kotest StringSpec, shouldBeApprox, Fixture):
- ConcaveSphereTest: hit from outside (1,0,0) t=1 normal LEFT (INWARD, the key difference vs Sphere); hit from inside far wall t=1 normal LEFT; miss; shadowHit; bbox.
- ConcavePartSphereTest: lower-hemisphere hit (0,-2,0) t=1.5 normal UP (inward); upper-hemisphere theta-rejection; miss; shadowHit; bbox.
- ThickRingTest: OUTER wall (3,1,0)->-x t=1 normal RIGHT; INNER wall from hollow (0,1,0)->+x t=1 normal LEFT; top rim straight-down t=1 normal UP; axial drop through central hole MISSES; bbox. (distinguishes the three sub-surfaces).
- BowlTest: OUTER wall from below t=1 normal DOWN; INNER concave wall t=0.5 normal UP; rim t=1 normal UP; wide miss; bbox lower-half.
- ObjectsScopeTest: 3 new DSL round-trip cases (concaveSphere/thickRing/bowl) via equals.
All derived values hand-computed from the geometry (not round-tripped). just test (clean check + detekt + jacoco) BUILD SUCCESSFUL; new code detekt-clean, no baseline entries. Pre-existing warnings (PlyReader cast, GridStructuresTest) unrelated.
AC STATUS: #2 and #3 met for delivered primitives. #1 PARTIAL: ConcaveSphere + ThickRing + Bowl correct; BeveledCylinder + BeveledWedge DEFERRED (blocked on existing Torus quartic phantom-root defect, see prior note) — left UNCHECKED honestly.
<!-- SECTION:NOTES:END -->
