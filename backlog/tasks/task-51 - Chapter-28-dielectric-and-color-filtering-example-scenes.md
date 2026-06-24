---
id: TASK-51
title: Chapter 28 dielectric and color-filtering example scenes
status: To Do
assignee: []
created_date: '2026-06-24 08:24'
labels:
  - book-coverage
  - examples
  - transparency
  - chapter-28
dependencies: []
references:
  - Chapter 28 Realistic Transparency _ Ray Tracing from the Ground Up.pdf
priority: low
ordinal: 54000
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
The Dielectric material (ch. 28: Fresnel + Beer-Lambert color filtering, nestable) is implemented but the only example is GlassSphere. Add scenes that demonstrate the capabilities unique to ch. 28. Scenes are auto-discovered WorldDefinition objects under src/examples (coverage-excluded; verify by rendering). Required primitives already exist (Sphere, SolidCylinder, Box/AlignedBox).
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Nested transparent objects (book Figure 28.15): three concentric Dielectric spheres (e.g. blue glass / lemon diamond / mauve water) with correct iorIn/iorOut for each glass-air, diamond-glass and water-diamond boundary plus per-medium filter colors, rendered at a high max recursion depth so the inner spheres show through correctly. This headline ch. 28 capability currently has no example
- [ ] #2 Color-filtering / Beer-Lambert demo: three overlapping solid cylinders with pure CMY filter colors over a white background (book Figure 28.8) and/or a row of colored transparent spheres of decreasing size that brighten as path length shrinks (book Figure 28.9)
- [ ] #3 Transparent box / glass blocks (book Figures 28.20, 28.22): a Box/AlignedBox with a Dielectric material at eta above sqrt(2), showing that faces adjacent to the viewed face act as mirrors (total internal reflection) and color deepens with path length
- [ ] #4 Each scene auto-registers, renders without errors with a non-black background, and is verified manually by rendering (examples/** coverage-excluded); detekt and the full build stay green
<!-- AC:END -->
