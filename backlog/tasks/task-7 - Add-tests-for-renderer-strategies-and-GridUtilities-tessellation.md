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

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Read GridUtilities.kt + renderer impls + existing RendererTest; capture baseline jacoco coverage.
2. AC#1: GridUtilitiesTest (commonTest) - flat+smooth tessellation across step counts: triangle counts 2*h*(v-1), pole-cap vertices, unit-sphere vertices, smooth radial per-vertex normals vs flat single face normal. Add Normal.shouldBeApprox to Fixture.kt.
3. AC#2: extend RendererTest with success + failure tests for ForkJoin/Coroutine/NaiveCoroutine/VirtualThread. Block renderers degrade silently (no throw) on sub-block resolutions -> pin observable shortfall, not a typed exception (verified via probe).
4. AC#3: equivalence test rendering a 32x32 positional reference through Sequential/ForkJoin/Parallel/NaiveCoroutine/Coroutine/VirtualThread; assert pixel-exact equality.
5. just test green; record before/after coverage.
<!-- SECTION:PLAN:END -->
