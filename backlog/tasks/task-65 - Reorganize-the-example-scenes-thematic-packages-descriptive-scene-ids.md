---
id: TASK-65
title: 'Reorganize the example scenes: thematic packages + descriptive scene ids'
status: Done
assignee: []
created_date: '2026-06-25 21:15'
updated_date: '2026-06-25 22:49'
labels: []
dependencies: []
ordinal: 68000
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
The src/examples scene tree is a half-finished migration. A clean thematic package tree already exists (materials/{dielectric,reflective,transparent}, lights/{ambient,area}, objects/beveled, textures/noise, acceleration, cameras, globalillumination, tracers), but ~20 scenes still sit flat in the root package, a mislabeled test/ package holds scenes (not tests), and ~25 scenes still carry opaque book-port WorldNN names.

KEY FACT: scene discovery is by id, scanned recursively (Worlds.kt classgraph-scans the whole examples package and keys worldMap by each WorldDefinition.id). So directory layout is cosmetic for discovery -- moving a file between subpackages only changes its package line + imports; the id and --world= key are untouched. Renaming a scene's id IS breaking (it is the --world= key) and must be done deliberately.

Agreed decisions (2026-06-25, with Joern):
- Add new subpackages where the tree lacks a home: objects/mesh (Bunny), lights/environment (EnvironmentLightScene), basics/ (the trivial Yellow* starter spheres).
- Distribute the mislabeled test/ package by theme (handled in the rename subtask, since its scenes are all WorldNN).
- Do the WorldNN -> descriptive-id rename now (breaks --world=WorldNN.kt keys).

CONVENTION CORRECTION: book provenance in this repo lives in the KDoc class comment (e.g. FishBowlScene, ColorFilteringCylinders cite 'Suffern §28.8, Figure 28.8'), NOT in scene metadata. So 'book figure in metadata' = a KDoc ref. Do NOT fabricate figure numbers: the NN in WorldNN is this repo's own port numbering, not Suffern figure numbers. Cite chapter/topic where the scene makes it recoverable, a figure only where genuinely inferable, and preserve the old id in the KDoc ('formerly World16.kt') so old --world= scripts stay greppable.

Split into two subtasks by risk: non-breaking package moves vs the breaking WorldNN rename.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 All example scenes live in a thematic subpackage; the root examples package retains only infrastructure (Worlds.kt), the scene Template, and the README hero scene (ExampleForGithub)
- [x] #2 No scene named WorldNN remains; the test/ package is gone
- [x] #3 Full check is green (./gradlew clean check) and every scene is still discoverable + renderable after the reorg
<!-- AC:END -->

## Final Summary

<!-- SECTION:FINAL_SUMMARY:BEGIN -->
Reorganized all example scenes. 65.1: moved 19 flat root scenes into the thematic tree (+ basics/objects.mesh/lights.environment), non-breaking. 65.2: renamed 24 WorldNN scenes to descriptive ids with KDoc provenance, removed test/, fixed the broken World60 area-light scene. Root examples package now holds only Worlds.kt, Template.kt, ExampleForGithub.kt; no WorldNN scenes remain; build green and all scenes discoverable/renderable.
<!-- SECTION:FINAL_SUMMARY:END -->
