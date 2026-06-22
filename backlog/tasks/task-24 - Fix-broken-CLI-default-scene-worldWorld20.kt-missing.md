---
id: TASK-24
title: Fix broken CLI default scene (--world=World20.kt missing)
status: Done
assignee:
  - '@claude'
created_date: '2026-06-22 14:20'
updated_date: '2026-06-22 15:19'
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

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
Bug confirmed: Clikt default --world=World20.kt (CommandLine.kt:20) is not among the classgraph-discovered scene ids (no World20.kt WorldDefinition exists; the World* objects present are World5/16/17/23b/24/26/27/28/30/31/32/32b/33/35/58/60/61/66b/71/71b/74/74kdt/75/80 — World20 absent). Clikt does not validate .default() against the .choice() set, so the no-arg run passed 'World20.kt' through to Render.render, which logged 'WorldDef World20.kt is not known' and wrote NO png (build still reported SUCCESS). Reproduced via ./gradlew run --args=--resolution=720p before the fix.

Fix (minimal, no CLI/scene-loading refactor): changed the Clikt default in CommandLine.kt to YellowAndRedSphere.kt — a simple Whitted-friendly scene (two matte/phong spheres + two planes + one point light; no mesh, no .ply download), already manually rendering correctly in TASK-8 notes. Also updated the option help text to name the default. Updated docs: CLAUDE.md (--world default line) and the AREA example commands in CLAUDE.md + README.md (which referenced the equally-absent World20.kt / World20AreaDisk.kt) now use AreaShadedSpheres.kt, a real AREA-light scene.

No core logic maps a missing/blank id to a default — worldDef() returns null and Render only warns — so the change is entirely the coverage-excluded Clikt-default + Main glue; verified manually per CLAUDE.md (no unit test added by design for the excluded zone).

Files changed: src/commonMain/kotlin/net/dinkla/raytracer/ui/CommandLine.kt; CLAUDE.md; README.md.

Manual verification (all produced valid PNGs, no 'not known' warning):
- ./gradlew run  -> Rendering YellowAndRedSphere.kt, saved 1920x1080 8-bit RGBA PNG.
- ./gradlew run --args=--resolution=720p -> 1280x720 8-bit RGBA PNG.
- ./gradlew run --args="--world=AreaShadedSpheres.kt --tracer=AREA --renderer=FORK_JOIN --resolution=720p" (docs example) -> 1280x720 PNG. file(1) confirmed all valid PNGs; generated test PNGs cleaned up.
just test (clean check + all tests + detekt + jacoco): BUILD SUCCESSFUL (green). Two pre-existing unchecked-cast warnings in PlyReader.kt + GridStructuresTest.kt are unrelated and untouched.

Out of scope / follow-up: README.md also lists two other now-stale scene ids in example commands — World66.kt (only World66b.kt exists) and World42.kt (absent). Left as-is per scope discipline; flagged for the manager to route as a separate doc-cleanup task.
<!-- SECTION:NOTES:END -->

## Final Summary

<!-- SECTION:FINAL_SUMMARY:BEGIN -->
Fixed the broken CLI default scene. The Clikt --world default in CommandLine.kt was World20.kt, which no auto-discovered WorldDefinition provides, so a no-argument render logged 'WorldDef World20.kt is not known' and silently wrote no PNG (Render only warns on a null worldDef; build still reported SUCCESS). Changed the default to the existing simple Whitted scene YellowAndRedSphere.kt; updated the option help string and the default reference in CLAUDE.md, plus the stale World20.kt/World20AreaDisk.kt AREA example commands in CLAUDE.md and README.md to a real area-light scene (AreaShadedSpheres.kt). AC1-3 all met. CLI/Main is a coverage-excluded zone, so verified manually (not by unit test): reviewer independently confirmed World20 is absent from src/examples (grep exit 1), YellowAndRedSphere.kt is a real discoverable id, and ran ./gradlew run --args='--resolution=720p' -> 'Rendering YellowAndRedSphere.kt', valid 1280x720 PNG, no warning; also rendered the AREA example (AreaShadedSpheres.kt, --tracer=AREA --renderer=FORK_JOIN) -> valid PNG. Minimal/in-scope (default value + help + docs only; no CLI or scene-loading refactor). Verified via just test (clean check + detekt + jacoco) BUILD SUCCESSFUL. Committed as 4740c5d. Follow-up flagged by implementer+reviewer (out of scope here): README.md still lists two stale scene ids in non-default example commands — World66.kt (only World66b.kt exists) and World42.kt (absent) — candidate small doc-cleanup, fits TASK-16's docs-cleanup theme.
<!-- SECTION:FINAL_SUMMARY:END -->
