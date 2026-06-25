---
id: TASK-65.2
title: >-
  Rename WorldNN scenes to descriptive ids with book provenance (breaking
  --world= keys)
status: Done
assignee: []
created_date: '2026-06-25 21:16'
updated_date: '2026-06-25 22:49'
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
- [x] #1 No scene id matches /^World\d+/; every renamed scene has a descriptive id == its object name == its filename
- [x] #2 Each renamed scene carries a KDoc book-provenance comment (chapter/topic, no fabricated figure numbers) and a 'formerly WorldNN.kt' note
- [x] #3 test/ package is removed; identified duplicates (e.g. World33/World80, World74/World74kdt) are resolved, not blindly renamed twice
- [x] #4 All in-repo references to old WorldNN.kt ids (README, docs, code defaults, audit tooling) are updated; repo grep for 'World\d+\.kt' is clean
- [x] #5 ./gradlew clean check is green and a sample of renamed scenes renders correctly under their new --world= keys
- [x] #6 World60 (-> TwoAreaLightsAndSpheres.kt): the 'does not work' cause is diagnosed and fixed, verified by a render (approved scope expansion 2026-06-26)
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

Executed: renamed+relocated all 24 WorldNN scenes (object name == id == filename kept in sync via scripted git mv + rewrite), emptied and removed test/, added a wrapped KDoc to each (topical provenance + 'formerly WorldNN.kt'; chapter cited only for ThinLensBoxes/ch.10 which the source already documented; kd-tree scenes noted as not-from-book; NO fabricated figure numbers). Updated references: BuilderTest imports+usages+test names (World17->ReflectiveCylindersAndBoxes, World23->AreaLitSpheres), README run-examples + DSL sample object (World48->BasicExample), arc42 CLI example, WorldDefinition KDoc example, DepthOfFieldDemo [World58]->[ThinLensBoxes] links, CommandLineTest synthetic id (World20.kt->Bunny.kt), and TwoSpheresAndTiltedPlane's stale metadata id label.

AC#4 nuance: remaining 'World<digits>' literals are NOT scene references and were intentionally left: FilenameUtilitiesTest uses 'World73'/'NewWorld3.kt' as filename-parser fixtures; NewWorld4 is a different scene (not /^World\d+/, out of scope).

World60 fix (TwoAreaLightsAndSpheres): root cause was a broken composition, not a one-liner -- camera viewed sphere fronts (+z) while area lights faced down (+y), green material had ka=0 (pure black), and no AREA tracer was set. Rewrote into a proper soft-shadow demo: 4 spheres on a floor, an angled top-down camera, a small (warm) and a large (magenta) downward area light -> crisp vs soft shadows; set preferredTracer(AREA). Verified by render (AREA tracer): coherent lit image with visible area-light shadows.

Follow-up candidates (out of scope, not done): NewWorld4 has an opaque name; TwoSpheresSinkIntoPlane/AreaShadedSpheres carry stale metadata id labels ('World6'/'World66 with area'); AmbientOccludedBunny loads the PLY via a Windows backslash path 'resources\\Bunny4K.ply'.
<!-- SECTION:NOTES:END -->

## Final Summary

<!-- SECTION:FINAL_SUMMARY:BEGIN -->
Renamed all 24 opaque WorldNN scenes to descriptive ids (object==id==filename), relocated them into the thematic tree, removed the mislabeled test/ package, and gave each a KDoc with topical book provenance + a 'formerly WorldNN.kt' note (no fabricated figure numbers). Updated all in-repo references (BuilderTest, README, arc42, WorldDefinition KDoc, DepthOfFieldDemo links, CommandLineTest). Diagnosed and fixed the broken World60 -> TwoAreaLightsAndSpheres into a working small/large area-light soft-shadow demo. Verified: ./gradlew clean check green; sample scenes render under their new --world= keys; the fixed scene renders a coherent area-lit image.
<!-- SECTION:FINAL_SUMMARY:END -->
