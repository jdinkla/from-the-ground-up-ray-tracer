---
id: TASK-65.1
title: Move flat root scenes into the thematic package tree (non-breaking)
status: To Do
assignee: []
created_date: '2026-06-25 21:15'
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
- [ ] #1 All ~20 listed root scenes moved to the mapped subpackages; objects/mesh, lights/environment, and basics packages created
- [ ] #2 Each moved scene keeps its exact id string; no --world= key changes in this subtask
- [ ] #3 Root examples package contains only Worlds.kt, Template.kt, ExampleForGithub.kt (plus the remaining WorldNN scenes, which the rename subtask handles)
- [ ] #4 ./gradlew clean check is green and a spot-check render of at least one moved scene (e.g. --world=Bunny.kt) still resolves and renders
<!-- AC:END -->
