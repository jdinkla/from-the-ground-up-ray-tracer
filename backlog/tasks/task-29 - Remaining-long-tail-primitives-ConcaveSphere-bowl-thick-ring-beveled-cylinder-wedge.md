---
id: TASK-29
title: >-
  Remaining long-tail primitives (ConcaveSphere, bowl/thick-ring, beveled
  cylinder/wedge)
status: In Progress
assignee:
  - '@claude'
created_date: '2026-06-22 17:40'
updated_date: '2026-06-22 20:08'
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
1. [DONE prior pass] ConcaveSphere, ConcavePartSphere, ThickRing, Bowl + DSL + tests — KEEP.
2. PART A — FIX TORUS PHANTOM ROOTS (cover-first behavior change). Root cause: solveQuartic returns spurious real roots under poor conditioning for near-axis rays (nonNegativeSqrt clamps neg discriminants, fabricating roots). Probe confirmed Torus(0.9,0.1) downward axis ray => roots with implicit/quartic residual 2.56 (not on surface). Fix: validate+polish each candidate root — Newton-polish against the quartic, then reject roots whose hit point fails the torus implicit equation (surface residual > tol). Genuine roots polish to ~1e-14; phantoms stay at ~0.7. Apply to Torus.hit/hitF AND PartTorus (same defect). Cover-first: failing test pinning correct behavior (axis ray misses, piercing ray hits at correct t), confirm fail on buggy code, then fix. Re-run TorusTest + PartTorusTest; update any assertion that pinned a phantom value (call out old->new).
3. PART B — BeveledCylinder (compound): shortened OpenCylinder body (y0+rb..y1-rb, radius) + 2 shrunk Disk caps (radius-rb at y0/y1) + Torus(radius-rb, rb) rim translated via Instance to y=y0+rb and y=y1-rb. bbox radius x [y0,y1]. DSL beveledCylinder + cover-first tests (body/cap/rim hit, miss, bbox).
4. PART B — BeveledWedge (compound): PartCylinder outer wall over [phiMin,phiMax] + 2 part-annulus caps + 2 Rectangle radial sides + PartTorus rim bevels at top/bottom. DSL beveledWedge + cover-first tests.
5. just test green incl detekt; no baseline entries; keep hit() helpers under thresholds.
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

PART A diagnosis (probe, then removed): solveQuartic returns PHANTOM real roots for near-axis/vertical rays on a thin torus. Confirmed on Torus(0.9,0.1) with a downward ray from y=3 on the central axis (x=0): solveQuartic yields 4 roots at t~2.1056/3.8944 (each duplicated) whose hit points have torus implicit-equation residual = 2.56 (far from 0) and whose quartic-eval at those t is also 2.56 — i.e. the solver returned values that are not even roots of the polynomial it was handed (the resolvent path's nonNegativeSqrt clamps negative discriminants to 0, fabricating roots). Genuine roots have residual ~2e-4 and Newton-polish to ~1e-14 ON the surface; phantoms Newton-polish but stay OFF the surface (~0.7). Fix = polish-then-surface-validate each candidate root.
<!-- SECTION:NOTES:END -->
