---
id: TASK-52
title: 'Glass of water: compound dielectric object + example scene'
status: To Do
assignee: []
created_date: '2026-06-24 08:24'
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
