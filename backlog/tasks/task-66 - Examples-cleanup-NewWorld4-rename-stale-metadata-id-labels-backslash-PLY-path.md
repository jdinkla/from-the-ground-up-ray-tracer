---
id: TASK-66
title: >-
  Examples cleanup: NewWorld4 rename, stale metadata id labels, backslash PLY
  path
status: In Progress
assignee:
  - '@claude'
created_date: '2026-06-26 20:37'
updated_date: '2026-06-26 21:02'
labels: []
dependencies: []
ordinal: 71000
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Three small leftovers surfaced during the example-scene reorg (TASK-65). Grouped into one cleanup PR.

1. RENAME NewWorld4 (breaking --world= key). src/examples/.../lights/area/NewWorld4.kt is opaquely named like the WorldNN scenes were (it is a DiskLight area-light scene: an emissive disk lighting a yellow matte object). Rename it to a descriptive id following the TASK-65.2 convention -- object name == id == filename, add a KDoc with topical provenance + a 'formerly NewWorld4.kt' note. Suggested name: DiskAreaLight.kt (confirm by reading the scene). Update any in-repo references (grep for NewWorld4).

2. FIX stale metadata id() labels. Some scenes carry a metadata { id("...") } *label* still referencing dead World numbers, inconsistent with their real WorldDefinition.id:
   - lights/area/AreaShadedSpheres.kt -> id("World66 with area")
   - materials/reflective/TwoSpheresSinkIntoPlane.kt -> id("World6")
   - lights/area/NewWorld4.kt -> id("New World 4 - emissive") (handle with its rename)
   Set each metadata id() to match the scene's real id/name (or drop the redundant label). Grep all scenes for other metadata id() labels that don't match their scene and fix those too.

3. FIX backslash PLY path (real bug on non-Windows). lights/ambient/AmbientOccludedBunny.kt loads the mesh via fileName = "resources\\Bunny4K.ply" (backslash) -- this fails to resolve on macOS/Linux. Every other bunny scene uses the forward-slash 'resources/Bunny4K.ply' (Bunny.kt, BunnyRowGrid.kt, BunnyRowKdTree.kt). Change it to a forward slash.

examples/** is coverage-excluded, so verify manually per CLAUDE.md: render the renamed scene by its new --world= key, render AmbientOccludedBunny to confirm the PLY now loads, and run ./gradlew clean check.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 NewWorld4 renamed to a descriptive id (object name == id == filename) with a KDoc provenance + 'formerly NewWorld4.kt' note; no scene id named NewWorldN remains; in-repo references updated
- [x] #2 No scene's metadata id() label references a dead World number; the three identified labels (AreaShadedSpheres, TwoSpheresSinkIntoPlane, NewWorld4) and any others found by grep are corrected or removed
- [x] #3 AmbientOccludedBunny loads the PLY via a forward-slash path; it renders (PLY found) on this machine
- [x] #4 ./gradlew clean check is green; the renamed scene and AmbientOccludedBunny both render under their --world= keys
<!-- AC:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
1) Renamed NewWorld4.kt -> DiskAreaLight.kt (git mv): object DiskAreaLight, id 'DiskAreaLight.kt', metadata id('DiskAreaLight'), added KDoc with topical provenance + 'Formerly NewWorld4.kt'. No other in-repo .kt references existed (only backlog task files, left as historical record). 2) Fixed stale metadata labels referencing dead World numbers: AreaShadedSpheres id('World66 with area')->id('AreaShadedSpheres'); TwoSpheresSinkIntoPlane id('World6')->id('TwoSpheresSinkIntoPlane'); grep also found YellowAndOrangeSphere id('World 10')+title('World 10') -> id('YellowAndOrangeSphere')+title('Yellow and Orange Sphere'). (Note: did NOT use the id(id) self-reference pattern from TwoAreaShadedSpheres -- inside metadata{} 'id' resolves to MetadataScope.id (empty), so that pattern silently sets an empty label.) 3) AmbientOccludedBunny: 'resources\\Bunny4K.ply' -> 'resources/Bunny4K.ply'. Manual verification (examples are coverage-excluded): clean check green; rendered AmbientOccludedBunny.kt at 720p -> PLY loaded, mesh grid built, PNG saved; rendered DiskAreaLight.kt by its new --world= key at 720p -> world resolved and rendered. Cleaned up the scratch render PNGs.
<!-- SECTION:NOTES:END -->
