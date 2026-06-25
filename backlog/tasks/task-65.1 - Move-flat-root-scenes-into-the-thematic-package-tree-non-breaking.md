---
id: TASK-65.1
title: Move flat root scenes into the thematic package tree (non-breaking)
status: Done
assignee: []
created_date: '2026-06-25 21:15'
updated_date: '2026-06-25 21:30'
labels: []
dependencies: []
parent_task_id: TASK-65
ordinal: 69000
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Move the ~20 descriptively-named scenes that still sit flat in the root examples package into the thematic tree. NON-BREAKING: only the package declaration + imports change; each scene's id (= --world= key) is a hardcoded string that stays exactly as-is. WorldNN scenes are OUT OF SCOPE here (handled in the rename subtask). Verify discovery still works via classgraph after the move.

Move mapping (file -> destination subpackage):
- SpheresOnABlackMirror, TwoSpheresSinkIntoPlane -> materials/reflective
- TransparentSpheres -> materials/transparent
- TextureMaterialsScene (marble/noise) -> textures/noise
- SparseGridScene -> acceleration
- FishEyeScene, SphericalScene -> cameras
- AmbientOccludedSphere -> lights/ambient
- InstanceExample, PartObjects, VariousObjects, VariousObjects2 -> objects
- SolidsAndBeveled, ManyWireframeCubes -> objects/beveled   (ManyWireframeCubes is the one judgment call: wireframe boxes; could arguably be acceleration -- confirm on the way through)
- Bunny -> objects/mesh   (NEW package)
- EnvironmentLightScene -> lights/environment   (NEW package)
- YellowAndRedSphere, YellowAndOrangeSphere, YellowSpheres -> basics   (NEW package; YellowAndRedSphere is the CLI default scene)

Stay in the root package by design: Worlds.kt (infra), Template.kt (scene template), ExampleForGithub.kt (README hero image / generic showcase).

Note: examples/** is JaCoCo/coverage-excluded glue, so per CLAUDE.md verify manually rather than adding unit tests -- after moving, render or list scenes and confirm each moved scene is still discoverable by its unchanged id.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 All ~20 listed root scenes moved to the mapped subpackages; objects/mesh, lights/environment, and basics packages created
- [x] #2 Each moved scene keeps its exact id string; no --world= key changes in this subtask
- [x] #3 Root examples package contains only Worlds.kt, Template.kt, ExampleForGithub.kt (plus the remaining WorldNN scenes, which the rename subtask handles)
- [x] #4 ./gradlew clean check is green and a spot-check render of at least one moved scene (e.g. --world=Bunny.kt) still resolves and renders
<!-- AC:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
Moved 19 flat root scenes into the thematic tree via git mv + line-1 package rewrite (ids/--world= keys untouched). New packages created: basics (YellowAndRedSphere, YellowAndOrangeSphere, YellowSpheres), objects/mesh (Bunny), lights/environment (EnvironmentLightScene). ManyWireframeCubes confirmed as objects/beveled (beveledBox isWiredFrame=true; the grid{} is just the accel container).

Found + fixed compile-time FQN imports of moved scenes in tests: BuilderTest (AmbientOccludedSphere, InstanceExample, TransparentSpheres, VariousObjects, YellowAndRedSphere) and RenderStatsTest (YellowAndRedSphere); re-sorted import block. Other hits (CommandLine default, Render help text, README) reference the unchanged id string, not the package.

Root examples package now holds only Worlds.kt, Template.kt, ExampleForGithub.kt (kept by design). ./gradlew clean check green (detekt + tests; pre-existing unchecked-cast warnings unrelated). Discovery+render verified: --world=YellowAndRedSphere.kt rendered from its new basics package in 447ms; artifact cleaned up.
<!-- SECTION:NOTES:END -->

## Final Summary

<!-- SECTION:FINAL_SUMMARY:BEGIN -->
Moved 19 flat root example scenes into the thematic package tree (git mv + line-1 package rewrite; scene ids / --world= keys unchanged). Added packages: basics, objects/mesh, lights/environment. Fixed the only compile-time fallout — FQN scene imports in BuilderTest + RenderStatsTest. Root examples package now holds only Worlds.kt, Template.kt, ExampleForGithub.kt. Verified: ./gradlew clean check green, and --world=YellowAndRedSphere.kt still discovered + rendered from its new basics package.
<!-- SECTION:FINAL_SUMMARY:END -->
