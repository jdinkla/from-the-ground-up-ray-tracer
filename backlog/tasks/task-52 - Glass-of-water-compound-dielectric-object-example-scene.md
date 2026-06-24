---
id: TASK-52
title: 'Glass of water: compound dielectric object + example scene'
status: In Progress
assignee:
  - '@claude'
created_date: '2026-06-24 08:24'
updated_date: '2026-06-24 10:50'
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
- [ ] #1 A GlassOfWater compound object is added from the existing part primitives, with correctly oriented normals and three Dielectric materials for the glass-air, water-air and water-glass boundaries plus filter colors
- [ ] #2 A new example scene renders the glass of water (optionally with a Matte straw) over a checker plane at a high max recursion depth (book Figure 28.38), showing refraction, TIR on the water surface and color filtering; a straw appears to bend at the water line
- [ ] #3 Reusable geometry/assembly logic that lands in commonMain is covered by frozen unit tests (cover-first, specs/testing.md); the scene (examples/**) is verified manually by rendering; detekt and the full build stay green
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Add GlassOfWater compound in commonMain (objects/compound/) assembled from PartCylinder/Disk/Annulus/PartTorus part surfaces; constructor takes the three Dielectric materials (glass-air, water-air, water-glass) and assigns each per-part, overriding Compound's single-material propagation. Boundaries per Suffern 28.7: glass-air {top annulus, outer convex wall, inner concave wall above water, bottom disk}; water-glass {inner convex wall below water, cavity bottom disk}; water-air {water-surface disk}; plus quarter-torus meniscus.
2. Cover-first frozen unit test (GlassOfWaterTest, StringSpec) for assembly invariants: part count, bounding box extents, per-boundary material assignment, representative hits (t + normal) on outer wall / water surface / bottom, equals/hashCode/toString.
3. Add a glassOfWater DSL method to ObjectsScope taking the three material ids, building GlassOfWater and adding it via the no-material add() path (so per-part materials survive).
4. Add example scene GlassOfWater.kt under examples/materials/dielectric: checker plane, three dielectric materials + filter colors, optional Matte straw instance crossing the water line, maxDepth(12), preferredTracer(WHITTED), non-black background. Double color literals only.
5. Run ./gradlew test for new test; render scene at 720p WHITTED, confirm refraction/TIR/color-filtering/bending straw; clean up PNGs. Then ./gradlew clean check (just test) green.
<!-- SECTION:PLAN:END -->
