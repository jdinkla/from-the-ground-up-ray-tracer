---
id: TASK-4
title: Replace unsafe non-null assertion (bang-bang) operators
status: In Progress
assignee:
  - '@claude'
created_date: '2026-06-22 09:11'
updated_date: '2026-06-22 11:06'
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
- [x] #1 No non-null assertion operators remain in production code, or each surviving one has a documented enforced invariant
- [x] #2 NPE-prone paths use requireNotNull with descriptive messages where a null is genuinely illegal
- [x] #3 detekt and tests pass
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

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
Removed all 70 production-code !! operators across commonMain + jvmMain.

Approach by category:
- requireNotNull(x) { msg } where null is genuinely illegal (a programming/config error): AreaLight (source, material, Sample.samplePoint/lightNormal/wi), EnvironmentLight (sampler, material), Matte/Phong (Sample.wi extracted once per loop iteration), arealights DiskLight/RectangleLight (material), ObjectsScope.ply (materials[id] lookup -> message includes the missing id), KDTree.hit (root must be built by initialize()), Render + Swing (world.renderer must be wired by context.adapt), SampledSingleRayRenderer/SimpleSingleRayRenderer (lens ray, message includes pixel), all KDTree builders (SpatialMedianBuilder default + the three unused/dead builders ObjectMedian2Builder, TestBuilder, Test2Builder: voxel/split/objects invariants).
- Redundant-cast cleanups (behavior-preserving): Statistics.cs dropped 'as Leaf?'/'as InnerNode?' and used the smart-cast variable directly; MeshTriangle.computeNormal computes into a local val then assigns once (the property's non-public setter blocked smart-cast across the reassignment).
- Mesh.computeMeshNormals: vertexFaces[index]!! -> requireNotNull; PlyReader pre-fills every index with an empty list, so this enforces the existing invariant with a clear message.

Behavior preserved: every !!->requireNotNull keeps throw-on-null semantics (now with a descriptive message). No null was silently swallowed; no rendering output path changed. Pre-existing latent bugs were left intact on purpose (e.g. Test2Builder/TestBuilder.calcSplit pass .toMutableList() copies to splitByAxis) - out of scope.

detekt note: UnsafeCallOnNullableType is 'active' in detekt-config.yml but the :detekt task has no type-resolution classpath, so it never flagged !! - there were NO baseline suppressions for it to remove. Fixed 3 MaxLineLength violations I introduced.

Cover-first: the only logic-restructuring touches (Mesh/MeshTriangle smooth-normal path, ObjectsScope materials lookup) are already characterized by PlyReaderTest (6/6, incl. 'read smooth') and ObjectsScopeTest (16/16); both passed unchanged against the new code. Swing change (coverage-excluded) is a literal renderer!!->requireNotNull swap, verified by compile.

Verification: just test (./gradlew clean check) GREEN - compile + all tests + detekt. Two remaining warnings (PlyReader unchecked cast, a test unchecked cast) are pre-existing and unrelated.
<!-- SECTION:NOTES:END -->
