---
id: TASK-4
title: Replace unsafe non-null assertion (bang-bang) operators
status: In Progress
assignee:
  - '@claude'
created_date: '2026-06-22 09:11'
updated_date: '2026-06-22 11:00'
labels:
  - reliability
  - refactor
dependencies: []
references:
  - docs/arc42/11_risks_and_technical_debt.md
priority: high
ordinal: 4000
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
There are 70+ uses of the non-null assertion operator across production code (notably AreaLight.kt, materials Phong.kt/Matte.kt, and KDTree builders). Each is a potential mid-render NullPointerException. Replace with safe calls, requireNotNull with a message, or enforce non-null invariants by construction.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 No non-null assertion operators remain in production code, or each surviving one has a documented enforced invariant
- [ ] #2 NPE-prone paths use requireNotNull with descriptive messages where a null is genuinely illegal
- [ ] #3 detekt and tests pass
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Inventory all !! in commonMain+jvmMain (done: ~70 sites across AreaLight, EnvironmentLight, Matte, Phong, KDTree builders, KDTree.hit, Statistics, renderers, Render, ObjectsScope, Mesh, MeshTriangle, arealights, Swing).
2. Classify each: (a) requireNotNull with descriptive message where null is genuinely illegal (AreaLight.material/source/Sample fields, arealights material, ObjectsScope materials lookup, lens ray, KDTree.root, Render.renderer, builder split/voxel/objects); (b) redundant smart-cast cleanups (Statistics, MeshTriangle); (c) keep nullable-by-design but require at use site.
3. detekt has UnsafeCallOnNullableType active but no type-resolution classpath, so !! is not currently flagged; AC means no new violations + tests stay green. No baseline suppressions to remove.
4. Apply changes file-by-file, preserving behavior (same throw-on-null semantics). Run targeted tests after each slice.
5. Swing file (ui/swing) is coverage-excluded; replace !! there too and verify by compile (no manual render needed since it is a literal swap with identical semantics).
6. Run just test (clean check) - must be green.
<!-- SECTION:PLAN:END -->
