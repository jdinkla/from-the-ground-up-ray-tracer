---
id: TASK-65.2
title: >-
  Rename WorldNN scenes to descriptive ids with book provenance (breaking
  --world= keys)
status: In Progress
assignee: []
created_date: '2026-06-25 21:16'
updated_date: '2026-06-25 22:32'
labels: []
dependencies:
  - TASK-65.1
parent_task_id: TASK-65
ordinal: 70000
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Rename the ~25 remaining WorldNN scenes (opaque book-port numbering) to descriptive ids. BREAKING: a scene's id is its --world= CLI key, so every --world=WorldNN.kt invocation, and any README/docs/scripts referencing them, must be updated. Keep the .kt suffix on the new id (e.g. OpenCylinderOnPlane.kt) to limit the breakage to names only.

Per WorldNN scene, do all of:
1. Read it and give it a descriptive id + matching object name + filename (kept in sync; the id is currently a hand-copied string equal to the filename).
2. Relocate to the correct thematic subpackage (this also empties and removes the mislabeled test/ package).
3. Add/extend the KDoc class comment with book provenance following the existing convention (cite Suffern chapter/topic; a figure number ONLY where genuinely inferable -- DO NOT fabricate). Include 'formerly WorldNN.kt' so old --world= scripts stay greppable.
4. Update any reference to the old id (README.md, docs, the CLI default in code if applicable, audit tooling).

WorldNN inventory (current package -> feature sketch from profiling; confirm by reading):
- acceleration/World75 (spheres, 3 point lights)
- cameras/World58 (thin-lens camera, ch.10 per its comment; boxes+instances)
- lights/ambient/World61 (ambientOccluder; reflective compound)
- lights/area/World23 (areaLight + emissive; boxes/disk/rectangle), World60 (2 areaLights + emissive; spheres/rectangles)
- materials/reflective/World17, World27, World33, World80 (reflective spheres/compounds) -- World33 and World80 have IDENTICAL feature profiles: read both; if duplicates, dedup rather than inventing two names
- materials/transparent/World35 (transparent + box), World71, World71b (b is a reduced variant of 71 -- decide keep-both vs merge)
- objects/World16 (openCylinder on plane), World24 (near-empty: matte + plane?), World26 (single instance), World30 (compound+instances), World31 (10 instances; reflective+phong spheres), World32 / World32b (instances; b is a variant)
- test/World5 (3 spheres), World28 (instances; reflective), World66b (5 phong spheres + reflective -- near-identical to SpheresOnABlackMirror; check for duplication), World74 / World74kdt (IDENTICAL profiles -- almost certainly the same scene with grid vs kd-tree acceleration; resolve: one scene, or two clearly-named accel variants)

Naming decisions to surface to Joern before mass-renaming: the proposed id table, and the dedup calls (World33/80, World74/74kdt, World66b vs SpheresOnABlackMirror, World71/71b, World32/32b).

Examples/** is coverage-excluded, so verify manually: after renaming, render a sample of the renamed scenes by their new --world= keys and confirm they resolve and look unchanged. Also grep the repo for any lingering reference to an old WorldNN.kt id.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 No scene id matches /^World\d+/; every renamed scene has a descriptive id == its object name == its filename
- [ ] #2 Each renamed scene carries a KDoc book-provenance comment (chapter/topic, no fabricated figure numbers) and a 'formerly WorldNN.kt' note
- [ ] #3 test/ package is removed; identified duplicates (e.g. World33/World80, World74/World74kdt) are resolved, not blindly renamed twice
- [ ] #4 All in-repo references to old WorldNN.kt ids (README, docs, code defaults, audit tooling) are updated; repo grep for 'World\d+\.kt' is clean
- [ ] #5 ./gradlew clean check is green and a sample of renamed scenes renders correctly under their new --world= keys
- [ ] #6 World60 (-> TwoAreaLightsAndSpheres.kt): the 'does not work' cause is diagnosed and fixed, verified by a render (approved scope expansion 2026-06-26)
<!-- AC:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
Dedup investigation (diffed all suspected pairs). NO true duplicates to delete:
- World33 vs World80: same RGB-sphere layout but different reflectivity (World33 diffuse+reflective kd=1; World80 pure mirror kd=0, varying kr). Distinct scenes.
- World32 vs World32b: same instanced smooth-triangle geometry; World32 phong, World32b reflective. Distinct.
- World71 vs World71b: 71b is a reduced variant of 71. Distinct.
- World66b vs SpheresOnABlackMirror: same receding-spheres layout but World66b uses 3 colored directional lights + blue mirror + ambient 0.0. Distinct.
- World74 vs World74kdt: TRULY identical except Acceleration.GRID vs KDTREE -> a deliberate grid/kd-tree pair; keep both, name as a pair.
Also: World60 metadata is description('does not work') -> known-broken scene, needs a decision. World61 loads the bunny via a Windows path 'resources\\Bunny4K.ply' (backslash) -> latent portability bug, out of scope (follow-up). Several WorldNN are imported by FQN in BuilderTest (e.g. World17, World23) -> their imports must be updated on rename.

Approved name table (24 scenes) + decision to FIX World60 rather than drop/keep-broken. Proposed ids recorded in the conversation; executing the rename now.
<!-- SECTION:NOTES:END -->
