---
id: TASK-24
title: Fix broken CLI default scene (--world=World20.kt missing)
status: In Progress
assignee:
  - '@claude'
created_date: '2026-06-22 14:20'
updated_date: '2026-06-22 15:13'
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
- [ ] #1 Running the render with no --world argument resolves to an existing, auto-discovered scene and renders successfully
- [ ] #2 The default scene id referenced in code matches an actually-discoverable WorldDefinition
- [ ] #3 CLAUDE.md and CLI help text reflect the correct default scene id
<!-- AC:END -->
