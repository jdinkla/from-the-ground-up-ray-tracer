---
id: TASK-53
title: 'Fishbowl: compound dielectric object + example scene'
status: Done
assignee:
  - '@claude'
created_date: '2026-06-24 08:24'
updated_date: '2026-06-24 11:19'
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

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
Implemented FishBowl as a reusable compound in commonMain (objects/compound/FishBowl.kt), modelled per Suffern §28.8 (Figs 28.39/28.41) as boundary surfaces (not a solid water blob in a glass shell). Five part surfaces grouped by the two media each separates, each carrying its own Dielectric: glass-air {outer PartSphere theta in [openingAngle, PI]; inner ConcavePartSphere above water theta in [openingAngle, thetaWater]; quarter-PartTorus rim instanced to the opening height}; water-glass {submerged inner PartSphere theta in [thetaWater, PI]}; water-air {flat water-surface Disk radius sqrt(innerRadius^2 - waterY^2) at y=waterY}. thetaWater = acos(waterY/innerRadius). The three materials are passed to the constructor and assigned per part via a private addPart() that sets material directly on the part, bypassing Compound.material's single-material propagation (which would collapse all three to one). Mirrors GlassOfWater (TASK-52) exactly. Confirmed real constructor signatures of PartSphere/ConcavePartSphere(center,radius,phiMin,phiMax,thetaMin,thetaMax), PartTorus(a,b,phiMin,phiMax), Disk(center,radius,normal), Compound (objects list). The same normal-orientation design note from GlassOfWater applies: Dielectric re-derives the relative index from sign of n.wo per hit, so correct iorIn/iorOut per boundary drives the optics, not the part's convex/concave normal sign (so the task's 'convex part sphere for water-glass' is fine optically).

DSL: added ObjectsScope.fishBowl(glassAir, waterAir, waterGlass, geometry...) that resolves the three material ids and adds the compound via the no-material add() path so per-part materials survive. The default openingAngle is shared via FishBowl.Companion.DEFAULT_OPENING_ANGLE so DSL and constructor stay in sync (no duplicated magic number).

Scene: src/examples/.../dielectric/FishBowlScene.kt (id FishBowlScene.kt) over a green/white checker plane with a submerged Matte fish sphere; preferredTracer(WHITTED), maxDepth(15) (TASK-51 setter, Fig 28.41 high depth), bright far wall (non-black background), Double colour literals throughout.

Cover-first tests (commonMain, frozen):
- FishBowlTest (objects/compound, 14 cases): five surfaces; the three materials present (never collapsed); 3/1/1 counts per boundary (glass-air/water-glass/water-air); horizontal equator ray -> outer glass hit (t=3, +x normal, glassAir); axial down ray (top open) -> water surface (t=4.2, UP, waterAir); below ray -> outer glass bottom (t=3, DOWN, glassAir); bbox spans +/- outerRadius; wide-miss; equals/hashCode/inequality/null/type/toString. Confirmed RED (FishBowl unresolved) then GREEN.
- ObjectsScopeTest: new case verifying fishBowl(...) builds a single FishBowl whose parts retain all three distinct materials (the single-material-collapse guard at the DSL seam).

Verification: RED->GREEN cover-first confirmed. Rendered --world=FishBowlScene.kt --tracer=WHITTED --resolution=720p (1280x720, 163s): non-black, coherent spherical glass bowl open at the top on a checker floor; water surface band clearly visible with the checker refracted/magnified and bent below it; submerged fish refracted and displaced through water+glass; two filter tints (faint green glass region above, blue-green water below); TIR/reflection visible on the water surface. PNG cleaned up (pre-existing Jun 23 PNGs untouched). just test (= ./gradlew clean check) green; detekt clean; the two remaining warnings (PlyReader unchecked cast, GridStructuresTest unchecked casts) are pre-existing and unrelated.
<!-- SECTION:NOTES:END -->

## Final Summary

<!-- SECTION:FINAL_SUMMARY:BEGIN -->
Added a reusable FishBowl compound (commonMain, objects/compound/FishBowl.kt) modelling Suffern section 28.8's spherical fishbowl as an open-topped glass sphere partly filled with water: five boundary surfaces grouped 3/1/1 by the media each separates (glass-air = outer PartSphere + inner-above ConcavePartSphere + PartTorus rim ring; water-glass = submerged inner PartSphere; water-air = flat Disk water surface), each part carrying its own Dielectric material (glass-air iorIn 1.5, water-air iorIn 1.33, water-glass iorIn 1.33/iorOut 1.5) plus filter colors. Set per-part in the constructor, bypassing Compound's single-material propagation; added a parallel fishBowl(...) DSL adder (ObjectsScope) preserving them via the no-material add path, mirroring TASK-52's GlassOfWater. Added example FishBowlScene.kt (checker plane, submerged Matte fish, maxDepth(15), WHITTED) and frozen cover-first tests (FishBowlTest: 5 parts, 3/1/1 boundary counts, three distinct materials not collapsed, hit-based normal/orientation invariants for an open-topped bowl, bbox, miss, equals/hashCode/toString; plus an ObjectsScopeTest DSL case). Verified: ./gradlew clean check green, reviewer PASS (purely additive DSL change, materials retained, hit assertions hand-verified against primitive math, detekt baseline untouched), ./gradlew audit registers FishBowlScene non-black, and the 720p render shows refraction/magnification of the checker through the curved glass, a TIR water-surface band, two Beer's-law tints, and the displaced submerged fish (Fig 28.41). Committed dc4e83d.
<!-- SECTION:FINAL_SUMMARY:END -->
