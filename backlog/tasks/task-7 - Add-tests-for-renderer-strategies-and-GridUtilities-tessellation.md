---
id: TASK-7
title: Add tests for renderer strategies and GridUtilities tessellation
status: In Progress
assignee:
  - '@claude'
created_date: '2026-06-22 09:11'
updated_date: '2026-06-22 11:39'
labels:
  - testing
  - concurrency
dependencies: []
references:
  - docs/arc42/11_risks_and_technical_debt.md
  - TECH_DEBT.md
priority: high
ordinal: 7000
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
GridUtilities.kt is the single largest coverage gap (1282 missed instructions) - tessellation helpers for flat/smooth spheres are untested (loops, pole triangles, normals). Renderer threading paths are also uncovered: ForkJoinRenderer, CoroutineBlockRenderer, NaiveCoroutineRenderer, and VirtualThreadBlockRenderer need success and barrier/failure tests mirroring the existing ParallelRenderer tests.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 GridUtilities tessellation covered for flat and smooth spheres across varying step counts (loops, pole triangles, normals)
- [x] #2 ForkJoin, Coroutine, NaiveCoroutine, and VirtualThread renderers have success and failure/barrier tests
- [x] #3 All renderer strategies produce equivalent output for a small reference scene
<!-- AC:END -->
