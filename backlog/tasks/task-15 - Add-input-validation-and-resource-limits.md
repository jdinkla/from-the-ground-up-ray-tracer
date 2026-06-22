---
id: TASK-15
title: Add input validation and resource limits
status: In Progress
assignee:
  - '@claude'
created_date: '2026-06-22 09:12'
updated_date: '2026-06-22 16:10'
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
