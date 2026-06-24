---
id: TASK-51
title: Chapter 28 dielectric and color-filtering example scenes
status: In Progress
assignee:
  - '@claude'
created_date: '2026-06-24 08:24'
updated_date: '2026-06-24 10:35'
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
- [x] #2 Color-filtering / Beer-Lambert demo: three overlapping solid cylinders with pure CMY filter colors over a white background (book Figure 28.8) and/or a row of colored transparent spheres of decreasing size that brighten as path length shrinks (book Figure 28.9)
- [x] #3 Transparent box / glass blocks (book Figures 28.20, 28.22): a Box/AlignedBox with a Dielectric material at eta above sqrt(2), showing that faces adjacent to the viewed face act as mirrors (total internal reflection) and color deepens with path length
- [x] #4 Each scene auto-registers, renders without errors with a non-black background, and is verified manually by rendering (examples/** coverage-excluded); detekt and the full build stay green
<!-- AC:END -->



## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Study existing dielectric scenes (GlassSphere, InsideTransparentSphere, RefractiveCaustic) + Dielectric material + DSL signatures (MaterialsScope.dielectric, ObjectsScope.solidCylinder/alignedBox/instance). 2. Confirm DSL gap: no hook for backgroundColor or maxDepth (private setter, default 5). Follow TASK-50 precedent: non-black background via enclosing/backdrop geometry; work within default depth 5, surface the depth limitation for AC#1. 3. Add NestedTransparentSpheres.kt (AC#1, Fig 28.15): 3 concentric dielectric spheres blueGlass(1.5/1.0)/lemonDiamond(2.42/1.5)/mauveWater(1.33/2.42) with per-medium cfIn, white room. 4. Add ColorFilteringCylinders.kt (AC#2, Fig 28.8): 3 overlapping CMY solidCylinders via translated Instances over white floor+wall. 5. Add TransparentGlassBox.kt (AC#3, Figs 28.20/28.22): AlignedBox dielectric eta=1.5>sqrt(2) -> TIR mirror faces, green cfIn path-length tint, coloured spheres around. 6. Render each at 720p WHITTED, confirm non-black/coherent/effect; clean up PNGs. 7. ./gradlew clean check green.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
Added 3 auto-discovered WorldDefinition scenes under src/examples/.../materials/dielectric/ (coverage-excluded; verified by rendering, no unit tests per the cover-first exception for examples/**). NO production code changed.

Files added:
- NestedTransparentSpheres.kt (AC#1, Fig 28.15): 3 concentric Dielectric spheres - blueGlass(iorIn 1.5/iorOut 1.0, glass-air), lemonDiamond(2.42/1.5, diamond-glass), mauveWater(1.33/2.42, water-diamond), each with its own Beer's-law cfIn (blue/lemon/mauve). White room (checker floor + white wall). preferredTracer WHITTED.
- ColorFilteringCylinders.kt (AC#2, Fig 28.8): 3 Dielectric cylinders with pure CMY cfIn (cyan/magenta/yellow) laid end-on (rotate 90deg about x) in a Venn triangle over a white floor+wall via translated Instances. Near-index-matched (iorIn 1.05) so rays pass straight through and pure Beer-Lambert absorption dominates. Renders as the canonical subtractive demo: overlaps read blue/green/red, triple overlap dark.
- TransparentGlassBox.kt (AC#3, Figs 28.20/28.22): Dielectric AlignedBox at iorIn 1.5 (eta > sqrt(2)) so side faces TIR-mirror; green cfIn deepens with path length; coloured spheres around for the mirror faces to reflect; checker floor + warm wall backdrop.

DSL gap (same as TASK-50): no DSL hook for backgroundColor or maxDepth (ViewPlane.maximalRecursionDepth has a private setter, default 5). Non-black backgrounds via enclosing/backdrop geometry (established convention). All Double color literals c(...) to avoid the Int-overload trap.

AC#1 DEPTH LIMITATION (decision needed): at the default maxDepth=5 the straight-through ray crosses 6 dielectric boundaries (3 in + 3 out) and is truncated to BLACK at the 6th, so the innermost (mauve water) sphere reads as a dark disk - the 'inner spheres show through correctly' part of AC#1 is NOT met. I empirically confirmed this: temporarily raising ViewPlane.maximalRecursionDepth to 15 (then reverted) renders the nested spheres correctly - mauve core visible, checker floor through the whole stack. The nested STRUCTURE and outer/middle per-medium tints ARE visible at depth 5. Fully satisfying AC#1 needs a maxDepth DSL hook (commonMain production change + cover-first test), which is outside this task's 'add example scenes' scope - hence NEEDS-DECISION.

Verification: rendered each at 720p WHITTED, all non-black/coherent/effect visible. ./gradlew audit health-renders: all 3 auto-registered (classgraph), Suspect (near-black) list EMPTY, Failed list EMPTY (90 scenes total). PNGs cleaned up from parent workspace dir.

CHECK: ./gradlew clean check (just test) -> BUILD SUCCESSFUL (detekt + all tests green). Two pre-existing unchecked-cast warnings (PlyReader.kt, GridStructuresTest.kt) unrelated to this change.
<!-- SECTION:NOTES:END -->
