---
id: TASK-53
title: 'Fishbowl: compound dielectric object + example scene'
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
ordinal: 56000
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
The book models a spherical fishbowl as a compound of part objects (book section 28.8, Figures 28.39, 28.41): a part-torus rim, a convex part sphere (outer glass), a concave part sphere (inner glass above the water), a convex part sphere (water-glass boundary) and a disk (water-air), with Dielectric materials for the glass-air, water-air and water-glass boundaries. The required primitives exist (PartSphere, ConcavePartSphere, PartTorus, Disk, Compound) but no FishBowl compound or scene exists.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 A FishBowl compound object is added from the existing part primitives with correctly oriented normals and the three Dielectric boundary materials
- [ ] #2 A new example scene renders the fishbowl with water over a plane at a high max recursion depth (book Figure 28.41), optionally containing a simple fish/object; refraction and color filtering are visible
- [ ] #3 Reusable geometry/assembly logic in commonMain is covered by frozen unit tests (cover-first, specs/testing.md); the scene (examples/**) is verified manually by rendering; detekt and the full build stay green
<!-- AC:END -->
