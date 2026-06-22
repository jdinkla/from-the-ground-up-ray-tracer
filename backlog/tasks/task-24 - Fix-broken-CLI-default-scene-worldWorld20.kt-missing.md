---
id: TASK-24
title: Fix broken CLI default scene (--world=World20.kt missing)
status: In Progress
assignee:
  - '@claude'
created_date: '2026-06-22 14:20'
updated_date: '2026-06-22 15:15'
labels:
  - bug
  - cli
dependencies: []
priority: medium
ordinal: 27000
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
The CLI/Swing default --world value is World20.kt (see CommandLine/Render and CLAUDE.md), but no scene object with id "World20.kt" is auto-discovered in the current scene set, so the documented default render (./gradlew run with no --world) fails to resolve a world. Pick a valid existing default scene id (or restore a World20.kt scene) so the no-argument invocation renders. Discovered while working TASK-8.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Running the render with no --world argument resolves to an existing, auto-discovered scene and renders successfully
- [x] #2 The default scene id referenced in code matches an actually-discoverable WorldDefinition
- [x] #3 CLAUDE.md and CLI help text reflect the correct default scene id
<!-- AC:END -->



## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Confirm bug: default --world=World20.kt in CommandLine.kt is not among classgraph-discovered ids (no World20.kt object exists); no-arg run logs 'WorldDef World20.kt is not known' and writes no PNG. CONFIRMED. 2. Change Clikt default in CommandLine.kt from World20.kt to an existing simple Whitted scene: YellowAndRedSphere.kt (matte/phong spheres + plane + point light, no mesh/.ply, already manually verified rendering in TASK-8). 3. Update docs/help: CLAUDE.md --world default line + example command, README.md example command, and the Clikt option help text to reflect the new default. 4. Manual verify: ./gradlew run (no args) + ./gradlew run --args=--resolution=720p both write a valid PNG. 5. just test green.
<!-- SECTION:PLAN:END -->
