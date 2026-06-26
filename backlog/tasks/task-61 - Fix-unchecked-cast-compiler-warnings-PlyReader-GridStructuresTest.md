---
id: TASK-61
title: Fix unchecked-cast compiler warnings (PlyReader + GridStructuresTest)
status: In Progress
assignee:
  - '@claude'
created_date: '2026-06-24 22:36'
updated_date: '2026-06-26 20:55'
labels:
  - tech-debt
  - build
dependencies: []
references:
  - TECH_DEBT_REPORT.md
priority: medium
ordinal: 64000
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
The build emits unchecked-cast warnings that are not treated as errors. PlyReader.kt:47 casts ArrayList<IGeometricObject> to ArrayList<MeshTriangle> in production: a latent ClassCastException / heap-pollution risk on malformed or mixed-geometry PLY input, exactly the untrusted-input path. GridStructuresTest.kt:260 and :420 cast Any! to Map<Int, Any> in tests. Because warnings do not fail the build, such issues accumulate invisibly. See TECH_DEBT_REPORT.md section P2 #5.

Locations: src/commonMain/.../utilities/PlyReader.kt:47; src/commonTest/.../acceleration/GridStructuresTest.kt:260 and :420.

Cover-first applies to the PlyReader change since it lives in commonMain: ensure a characterization test pins current PLY-parsing behavior before refactoring the cast.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 PlyReader.kt:47 cast is made type-safe (e.g. filterIsInstance / typed collection) so malformed or mixed-geometry PLY input cannot trigger a ClassCastException via this path
- [ ] #2 GridStructuresTest.kt:260 and :420 casts are made type-safe
- [ ] #3 No unchecked-cast warnings are emitted by compileKotlin or compileTestKotlin
- [ ] #4 A frozen characterization test covers the PlyReader behavior before the production cast is changed (cover-first)
- [ ] #5 ./gradlew clean check is green
- [ ] #6 Follow-up noted: consider enabling allWarningsAsErrors for commonMain once the core is warning-clean
<!-- AC:END -->
