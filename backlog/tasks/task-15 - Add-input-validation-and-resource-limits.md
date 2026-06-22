---
id: TASK-15
title: Add input validation and resource limits
status: Done
assignee:
  - '@claude'
created_date: '2026-06-22 09:12'
updated_date: '2026-06-22 16:14'
labels:
  - reliability
dependencies: []
references:
  - docs/arc42/11_risks_and_technical_debt.md
priority: low
ordinal: 15000
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
CLI arguments are parsed without bounds checking and large PLY models (Bunny4K.ply, Isis.ply) plus grid allocation can exhaust the heap with no limits. Validate CLI inputs (resolution, world-file existence) and add configurable bounds / graceful degradation for grid allocation and large-model loading.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Invalid CLI inputs (bad resolution, missing world) fail fast with a clear message
- [x] #2 Grid allocation and large-model loading have configurable limits and degrade gracefully instead of OOM
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. AC#1 (CLI): verified Clikt .choice already fails fast for bad --resolution and unknown --world (lists valid options, nonzero exit). Factor pure validation helpers into the testable core: Resolution.fromId(id) (clear message + valid ids) and validateWorldId/requireWorldDef in Render. Make Render.render(String,...) FAIL FAST (throw clear IllegalArgumentException pointing at how to list scenes) instead of only warning on a null worldDef. Unit-test both helpers.
2. AC#2 (Grid): add const MAX_NUM_CELLS (tens of millions) + constructor param maxNumCells. After deriving nx,ny,nz, if numCells>cap, clamp via a pure clampGridResolution helper (uniform downscale so numCells<=cap) and log a warning; never throw, never OOM. Cap set far above any real scene so frozen GridStructuresTest is byte-for-byte unchanged. Unit-test the clamp helper + confirm frozen Grid tests pass.
3. AC#2 (PLY): add const MAX_VERTICES/MAX_FACES (well above Isis ~47k verts / ~94k faces) + constructor params on PlyReader. In the header handler, if parsed vertex/face count exceeds the bound, throw a typed PlyLimitExceededException with a clear 'has N, exceeds M' message before allocating. Unit-test the bound check with a low test limit + tiny fabricated header (no real model load, no real OOM).
4. Run just test (clean check + detekt) green; keep new consts named (no MagicNumber).
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
AC#1 (fail-fast CLI validation): Verified the existing Clikt .choice() already fails fast for both bad --resolution and unknown --world (lists valid options, nonzero exit) — no regression there. Factored two pure, unit-tested validation helpers into the testable core: (a) Resolution.fromId(id) (commonMain Resolution.kt) throws IllegalArgumentException naming the bad value and listing valid ids; wired CommandLine.determineResolution to use it. (b) requireWorldDef(id, available) (commonMain Render.kt, internal top-level) throws a clear actionable message ('Unknown world ...; Run with --help to list available scenes'). Render.render(String,...) now FAILS FAST via requireWorldDef instead of only Logger.warn-ing on a null worldDef (the old TASK-24 silent-no-output behavior). Only Main uses that string overload (Swing uses worldDef()?.let{} + the WorldDefinition/film overloads), so no valid path is affected.

AC#2 (configurable limits, graceful degradation): GRID — added const DEFAULT_MAX_NUM_CELLS=64*1024*1024 (~64M) + constructor param maxNumCells on Grid (follows TASK-12 constructor-param pattern; propagated to nested sub-grids). After deriving nx,ny,nz, clampResolutionToCellCap() calls the pure companion helper clampGridResolution(nx,ny,nz,cap) which uniformly downscales (cube-root factor, each dim>=1, Long product to avoid Int overflow) so numCells<=cap, then logs a WARNING and proceeds — never throws, never OOMs. Cap is orders of magnitude above any real scene (heuristic targets a few cells/object), so frozen GridStructuresTest is unaffected. PLY — added consts DEFAULT_MAX_VERTICES=50_000_000 / DEFAULT_MAX_FACES=100_000_000 (well above the largest bundled model Isis: 46912 verts / 93820 faces; Bunny4K: 1889/3851) + constructor params maxVertices/maxFaces on PlyReader. In handleHeader, requireWithinLimit throws a typed PlyLimitExceededException ('PLY model declares N <kind> in line L, exceeds limit M') BEFORE the ensureCapacity allocation. Real models load unaffected.

Tested vs reasoned: UNIT-TESTED — clampGridResolution (within-cap unchanged / oversized fits under cap / extreme product keeps dims>=1), grid.initialize() with a low cap clamps the allocated cells array (RegressionStructuresTest), Resolution.fromId (known id + unknown id message), requireWorldDef (known + unknown message), PlyReader limit (vertex over, face over, at-limit boundary accepted). REASONED (not a flaky memory test, per task): the actual OOM-avoidance — caps bound the worst-case allocations below the heap-exhaustion threshold; no real 4K-model load or real OOM is triggered in any test.

Manual CLI verification (coverage-excluded zone): bad resolution -> 'Error: invalid value for --resolution: invalid choice: 999p. (choose from 480p, 720p, 1080p, 1440p, 2160p, 4320p)' nonzero exit; missing world -> 'Error: invalid value for --world: invalid choice: NoSuchScene.kt. (choose from ...)' nonzero exit; valid run (--world=YellowAndRedSphere.kt --resolution=480p) rendered and saved a timestamped PNG normally. Frozen GridStructuresTest passes unchanged. Full 'just test' (clean check + tests + detekt) green.
<!-- SECTION:NOTES:END -->

## Final Summary

<!-- SECTION:FINAL_SUMMARY:BEGIN -->
Added fail-fast input validation and configurable resource limits with no change to any valid-input/normal-scene behavior. AC#1: the CLI bad-resolution/unknown-world cases already failed fast via Clikt .choice(); factored the validation into pure, unit-tested core helpers (Resolution.fromId — names the bad value + lists valid ids; requireWorldDef) and changed Render.render(String,...) to fail fast with a clear actionable message on an unknown world id instead of the previous silent warn-and-write-nothing (closing the TASK-24 warn-on-null gap). Reviewer confirmed the only caller of the String overload is Main.render with a Clikt-validated id, and the Swing UI uses the WorldDefinition/film overloads + guards with worldDef(...)?.let, so the fail-fast change is safe. AC#2: Grid gains DEFAULT_MAX_NUM_CELLS (~64M = 67,108,864) + a maxNumCells ctor param; an over-cap nx*ny*nz is uniformly downscaled via the pure clampGridResolution (warns, never throws/OOMs), using left-to-right Long product math to avoid Int overflow — returns the input untouched below the cap, so normal scenes' nx/ny/nz and allocated arrays are byte-identical (reviewer verified; frozen GridStructuresTest assertions unchanged, TunableGrid's new maxNumCells param defaults to the cap). PlyReader gains DEFAULT_MAX_VERTICES (50M)/DEFAULT_MAX_FACES (100M) + ctor params, throwing a typed PlyLimitExceededException BEFORE the allocation on an oversized header — far above the real models (Isis ~47K/94K, Bunny4K ~1.9K/3.9K) so real loads are unaffected. Cover-first: unit-tested the clamp logic (product<=cap for over-cap, identity below), cap-on-initialize, PLY over-vertex/over-face/at-boundary (tiny fabricated headers + low test limits, no real model load / no real OOM), Resolution.fromId, and requireWorldDef (known→value, unknown→clear message); the actual OOM-avoidance is verified by reasoning (caps bound worst-case allocation), not a flaky memory test. Limits are named const vals (no MagicNumber suppressions). Production confined to Grid.kt, PlyReader.kt, Resolution.kt, Render.kt, CommandLine.kt. Verified via just test (clean check + detekt + jacoco) BUILD SUCCESSFUL; CLI manually verified (bad --resolution and missing --world fail fast with clear messages + nonzero exit; valid run renders normally). Committed as 3690261.
<!-- SECTION:FINAL_SUMMARY:END -->
