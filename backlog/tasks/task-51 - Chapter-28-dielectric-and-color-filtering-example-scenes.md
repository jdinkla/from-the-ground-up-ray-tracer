---
id: TASK-51
title: Chapter 28 dielectric and color-filtering example scenes
status: In Progress
assignee:
  - '@claude'
created_date: '2026-06-24 08:24'
updated_date: '2026-06-24 10:25'
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

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Study existing dielectric scenes (GlassSphere, InsideTransparentSphere, RefractiveCaustic) + Dielectric material + DSL signatures (MaterialsScope.dielectric, ObjectsScope.solidCylinder/alignedBox/instance). 2. Confirm DSL gap: no hook for backgroundColor or maxDepth (private setter, default 5). Follow TASK-50 precedent: non-black background via enclosing/backdrop geometry; work within default depth 5, surface the depth limitation for AC#1. 3. Add NestedTransparentSpheres.kt (AC#1, Fig 28.15): 3 concentric dielectric spheres blueGlass(1.5/1.0)/lemonDiamond(2.42/1.5)/mauveWater(1.33/2.42) with per-medium cfIn, white room. 4. Add ColorFilteringCylinders.kt (AC#2, Fig 28.8): 3 overlapping CMY solidCylinders via translated Instances over white floor+wall. 5. Add TransparentGlassBox.kt (AC#3, Figs 28.20/28.22): AlignedBox dielectric eta=1.5>sqrt(2) -> TIR mirror faces, green cfIn path-length tint, coloured spheres around. 6. Render each at 720p WHITTED, confirm non-black/coherent/effect; clean up PNGs. 7. ./gradlew clean check green.
<!-- SECTION:PLAN:END -->
