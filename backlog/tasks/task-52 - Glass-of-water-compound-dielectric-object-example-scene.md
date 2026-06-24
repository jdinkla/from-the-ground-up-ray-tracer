---
id: TASK-52
title: 'Glass of water: compound dielectric object + example scene'
status: Done
assignee:
  - '@claude'
created_date: '2026-06-24 08:24'
updated_date: '2026-06-24 11:04'
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
ordinal: 55000
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
The book models a glass of water as a compound of dielectric boundaries (book section 28.7, Figures 28.35 to 28.38): glass-air (annulus, concave cylinder, convex cylinder, disk), water-glass (convex cylinder, disk) and water-air (disk), with a quarter-torus meniscus and three Dielectric materials (glass-air eta_in 1.5, water-air eta_in 1.33, water-glass eta_in 1.33 / eta_out 1.5) plus filter colors. The required primitives exist (PartCylinder, Disk, Annulus, PartTorus, Compound) but no GlassOfWater compound or scene exists. Each boundary must be a single surface between two media with normals pointing out of each object (the book stresses you cannot model the water as a solid cylinder inside a ring).
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 A GlassOfWater compound object is added from the existing part primitives, with correctly oriented normals and three Dielectric materials for the glass-air, water-air and water-glass boundaries plus filter colors
- [x] #2 A new example scene renders the glass of water (optionally with a Matte straw) over a checker plane at a high max recursion depth (book Figure 28.38), showing refraction, TIR on the water surface and color filtering; a straw appears to bend at the water line
- [x] #3 Reusable geometry/assembly logic that lands in commonMain is covered by frozen unit tests (cover-first, specs/testing.md); the scene (examples/**) is verified manually by rendering; detekt and the full build stay green
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Add GlassOfWater compound in commonMain (objects/compound/) assembled from PartCylinder/Disk/Annulus/PartTorus part surfaces; constructor takes the three Dielectric materials (glass-air, water-air, water-glass) and assigns each per-part, overriding Compound's single-material propagation. Boundaries per Suffern 28.7: glass-air {top annulus, outer convex wall, inner concave wall above water, bottom disk}; water-glass {inner convex wall below water, cavity bottom disk}; water-air {water-surface disk}; plus quarter-torus meniscus.
2. Cover-first frozen unit test (GlassOfWaterTest, StringSpec) for assembly invariants: part count, bounding box extents, per-boundary material assignment, representative hits (t + normal) on outer wall / water surface / bottom, equals/hashCode/toString.
3. Add a glassOfWater DSL method to ObjectsScope taking the three material ids, building GlassOfWater and adding it via the no-material add() path (so per-part materials survive).
4. Add example scene GlassOfWater.kt under examples/materials/dielectric: checker plane, three dielectric materials + filter colors, optional Matte straw instance crossing the water line, maxDepth(12), preferredTracer(WHITTED), non-black background. Double color literals only.
5. Run ./gradlew test for new test; render scene at 720p WHITTED, confirm refraction/TIR/color-filtering/bending straw; clean up PNGs. Then ./gradlew clean check (just test) green.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
Implemented GlassOfWater as a reusable compound in commonMain (objects/compound/GlassOfWater.kt), modelled per Suffern §28.7 as boundary surfaces (not a solid water cylinder in a ring). Eight part surfaces grouped by the two media each separates, each carrying its own Dielectric: glass-air {top Annulus, outer PartCylinder, upper inner PartCylinder above water, bottom Disk}; water-glass {lower inner PartCylinder below water, cavity-floor Disk}; water-air {water-surface Disk + quarter-PartTorus meniscus instanced to the water height}. The three materials are passed to the constructor and assigned per part via a private addPart() that sets material directly on the part, deliberately bypassing Compound.material's single-material propagation (which would collapse all three to one). Project part primitives self-orient their normal toward the ray, so the book's hand-made convex/concave normal distinction is produced automatically; the dielectric still selects the correct medium per hit from the sign of n·wo, so the per-boundary iorIn/iorOut is what makes the optics right.

DSL: added ObjectsScope.glassOfWater(glassAir, waterGlass, waterAir, geometry...) that resolves the three material ids and adds the compound via the no-material add() path so the per-part materials survive (the standard add(material) path would override them).

Scene: src/examples/.../dielectric/GlassOfWaterScene.kt (id GlassOfWaterScene.kt) over a blue/white checker plane with a red Matte straw instanced (rotated 14deg about Z, slid sideways) crossing the water line; preferredTracer(WHITTED), maxDepth(12) (TASK-51 setter), bright far wall (non-black background), Double colour literals throughout.

Cover-first tests (commonMain, frozen):
- GlassOfWaterTest (objects/compound): eight surfaces; the three materials present (never collapsed); 4/2/2 counts per boundary; outer-wall hit (t, +x normal); axial ray meets water surface (t=1.6, UP, waterAir material); below-ray meets glass bottom (t=1.0, DOWN, glassAir material); bbox spans outer radius + full height; wide-miss; equals/hashCode/inequality/null/type/toString.
- ObjectsScopeTest: new case verifying glassOfWater(...) builds a single GlassOfWater whose parts retain all three distinct materials (the single-material-collapse guard at the DSL seam).

Verification: ./gradlew test (new classes) green; rendered --world=GlassOfWaterScene.kt --tracer=WHITTED --resolution=720p — non-black, coherent: refraction of the checker through glass+water, a clear TIR/reflection band at the water surface, two distinct Beer's-law filter tints (green glass above, blue-green water below), and the red straw visibly bent/offset at the water line. PNG cleaned up. just test (= ./gradlew clean check) green; the two remaining warnings (PlyReader unchecked cast, GridStructuresTest unchecked cast) are pre-existing and unrelated.
<!-- SECTION:NOTES:END -->

## Final Summary

<!-- SECTION:FINAL_SUMMARY:BEGIN -->
Added a reusable GlassOfWater compound (commonMain, objects/compound/GlassOfWater.kt) modelling Suffern section 28.7's glass of water as eight dielectric boundary surfaces grouped 4/2/2 by the two media each separates (glass-air = annulus + concave PartCylinder + convex PartCylinder + bottom Disk; water-glass = convex PartCylinder + Disk; water-air = water-surface Disk + quarter-PartTorus meniscus), each part carrying its own Dielectric material (glass-air iorIn 1.5, water-air iorIn 1.33, water-glass iorIn 1.33/iorOut 1.5) plus filter colors. Per-boundary materials are set per-part in the constructor and deliberately bypass Compound's single-material propagation; added a glassOfWater(...) DSL adder (ObjectsScope) that preserves them via the no-material add path. Design note: the part primitives don't all handcraft convex/concave normals, but the optics are still correct because Dielectric's Fresnel terms re-derive the relative index from the sign of n.wo per hit, so correct iorIn/iorOut per boundary is what matters (reviewer independently confirmed this is physically sound). Added example GlassOfWaterScene.kt (checker plane, bending Matte straw, maxDepth(12), WHITTED) and frozen cover-first tests (GlassOfWaterTest: 8 parts, 4/2/2 boundary counts, three distinct materials not collapsed, hit-based normal/orientation invariants, bbox, equals/hashCode/toString; plus an ObjectsScopeTest DSL case). Verified: ./gradlew clean check green, reviewer PASS (purely additive DSL change, no regression, detekt baseline untouched), ./gradlew audit registers the scene non-black, and the 720p render shows refraction, a TIR band at the water surface, two Beer's-law tints, and the straw bending at the water line. Committed 0444ec1.
<!-- SECTION:FINAL_SUMMARY:END -->
