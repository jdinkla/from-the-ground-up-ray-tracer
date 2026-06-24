---
id: TASK-53
title: 'Fishbowl: compound dielectric object + example scene'
status: In Progress
assignee:
  - '@claude'
created_date: '2026-06-24 08:24'
updated_date: '2026-06-24 11:14'
labels:
  - book-coverage
  - examples
  - transparency
  - objects
  - chapter-28
dependencies: []
references:
  - Chapter 28 Realistic Transparency _ Ray Tracing from the Ground Up.pdf
priority: low
ordinal: 56000
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
The book models a spherical fishbowl as a compound of part objects (book section 28.8, Figures 28.39, 28.41): a part-torus rim, a convex part sphere (outer glass), a concave part sphere (inner glass above the water), a convex part sphere (water-glass boundary) and a disk (water-air), with Dielectric materials for the glass-air, water-air and water-glass boundaries. The required primitives exist (PartSphere, ConcavePartSphere, PartTorus, Disk, Compound) but no FishBowl compound or scene exists.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 A FishBowl compound object is added from the existing part primitives with correctly oriented normals and the three Dielectric boundary materials
- [x] #2 A new example scene renders the fishbowl with water over a plane at a high max recursion depth (book Figure 28.41), optionally containing a simple fish/object; refraction and color filtering are visible
- [x] #3 Reusable geometry/assembly logic in commonMain is covered by frozen unit tests (cover-first, specs/testing.md); the scene (examples/**) is verified manually by rendering; detekt and the full build stay green
<!-- AC:END -->



## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Confirm constructor signatures of PartSphere/ConcavePartSphere/PartTorus/Disk/Compound (done). Mirror GlassOfWater/Bowl precedents.
2. Cover-first: write frozen FishBowlTest (StringSpec) for assembly invariants (5 parts; glass-air=3/water-glass=1/water-air=1; three distinct materials not collapsed; hit-based t+normal+material on outer glass equator, water surface disk, outer glass bottom; bbox = +/- outerRadius; wide-miss; equals/hashCode/toString). Confirm RED.
3. Add FishBowl compound in commonMain objects/compound/: glass sphere shell (outerRadius/innerRadius) with a top opening (theta in [thetaOpening, PI]) and water level waterY. Parts: outer PartSphere (glass-air), inner ConcavePartSphere above water (glass-air), PartTorus rim (glass-air), inner PartSphere below water (water-glass), water-surface Disk (water-air). Each part carries its own Dielectric via addPart() bypassing Compound single-material propagation. Confirm GREEN.
4. Add ObjectsScope.fishBowl(...) DSL adder via no-material add() path (preserves three materials); cover at the DSL seam in ObjectsScopeTest.
5. Add FishBowlScene example (examples/materials/dielectric): checker plane, three dielectrics + filter colours, maxDepth(high, Fig 28.41), preferredTracer(WHITTED), optional fish, non-black background. Double colour literals only.
6. Render --world=FishBowlScene.kt --tracer=WHITTED --resolution=720p; confirm non-black, refraction + colour filtering visible; clean up PNG. just test green.
<!-- SECTION:PLAN:END -->
