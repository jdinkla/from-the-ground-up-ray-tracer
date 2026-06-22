---
id: TASK-25
title: >-
  Fix silent underfill in block-based renderers for small/non-divisible
  resolutions
status: In Progress
assignee:
  - '@claude'
created_date: '2026-06-22 14:20'
updated_date: '2026-06-22 15:23'
labels:
  - bug
  - concurrency
  - renderer
dependencies: []
priority: medium
ordinal: 28000
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
ForkJoinRenderer, CoroutineBlockRenderer, and VirtualThreadBlockRenderer partition the film into a fixed block grid via Block.partitionIntoBlocks, where blockHeight/blockWidth = dimension/numBlocks truncates to 0 when the resolution is smaller than the block grid (writing 0 pixels), and silently drops remainder rows/columns for non-divisible resolutions. Only ParallelRenderer guards its input. Add a divisibility/min-resolution guard or remainder handling so every renderer fills the whole film (or fails loudly with a typed exception, consistent with ParallelRenderer) for any resolution. Discovered while working TASK-7.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Block renderers fill every pixel for resolutions smaller than the block grid, or throw a clear typed exception consistent with ParallelRenderer
- [x] #2 Non-divisible resolutions render the remainder rows/columns with no silently dropped pixels
- [x] #3 Tests cover sub-block-grid and non-divisible resolutions for each block renderer; the cross-renderer output-equivalence test still passes
- [x] #4 Update the TASK-7 RendererTest cases that currently pin the buggy underfill behavior to assert the corrected behavior
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Rewrite Block.partitionIntoBlocks to tile [0,width)x[0,height) exactly for any resolution/numBlocks: split each dimension into min(numBlocks,dim) contiguous segments whose sizes differ by at most 1 (ceil-then-remainder distribution); cross-product the x and y segments. No zero-size blocks, no gaps/overlaps, no dropped remainder. Returns empty list only for zero-area films.
2. Add BlockTest (jvmTest) asserting exact tiling for tricky sizes: width=5/blocks=8 (sub-grid), width=10/blocks=3 (non-divisible remainder), divisible case, and 1xN.
3. Update RendererTest TASK-7 cases: replace the three 'silently under-renders ... shouldBe 0' assertions (ForkJoin 4x4, Coroutine 8x8, VirtualThread 8x8) with full-coverage assertions, and update/repurpose the NaiveCoroutine 'block renderers leave empty' contrast test. Add per-renderer sub-block-grid + non-divisible coverage tests (every pixel exactly once).
4. Strengthen the output-equivalence test to also assert equivalence at a non-divisible resolution (e.g. 10x7).
5. Run ./gradlew test for renderer/Block tests, then full 'just test' (incl detekt) green.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
Fix: rewrote Block.partitionIntoBlocks (jvmMain/renderer/Block.kt) to tile [0,width)x[0,height) exactly for any resolution/numBlocks. Each dimension is split via a private splitDimension() into min(numBlocks,dimension) contiguous segments whose lengths differ by at most 1 (base = dim/segments, the first dim%segments segments get +1, distributing the remainder instead of dropping it). Capping the segment count at the dimension removes the zero-size blocks that integer truncation produced when dim<numBlocks. Blocks are the cross product of the x/y segment bounds. Empty list only for zero-area films; require(numBlocks>0). No change to ForkJoin/Coroutine/VirtualThread orchestration, per-pixel rendering, ParallelRenderer, Sequential, or NaiveCoroutine.

AC#4 — RendererTest assertion changes (old -> new):
- 'fork-join renderer silently under-renders ...' (4x4) writes==0  ->  'fills every pixel of a film smaller than its block grid': pixels.keys==fullCoverage AND writes==w*h.
- 'coroutine block renderer silently under-renders ...' (8x8) writes==0  ->  'fills every pixel ...': full coverage.
- 'virtual thread renderer silently under-renders ...' (8x8) writes==0  ->  'fills every pixel ...': full coverage.
- 'naive coroutine renderer fully renders a film that the block renderers leave empty' reworded to 'fully renders an 8x8 film' (same assertion writes==w*h; old premise — block renderers leave 8x8 empty — is no longer true).
These pinned a bug; behavior intentionally changed from silent under-fill to full fill.

New tests (AC#3): per renderer, added a non-divisible coverage test (ForkJoin 10x7; Coroutine/VirtualThread 50x33) asserting full coverage + exact write count; the former 'under-render' cases now serve as the sub-block-grid coverage tests. Added BlockTest.kt (8 cases): exact tiling for divisible (8x8/4), sub-grid (5x3/8, 1x1/8, 1x40/32), non-divisible (10x7/3), block sizes differ by <=1, zero-area -> no blocks, numBlocks<=0 rejected.

Equivalence: existing 32x32 cross-renderer test renamed and unchanged in substance (still passes). Added 'block renderers agree with the sequential reference for a non-divisible film' at 10x7 comparing ForkJoin/NaiveCoroutine/Coroutine/VirtualThread to SequentialRenderer pixel-for-pixel via PositionalSingleRayRenderer (ParallelRenderer excluded — it guards 10x7 by design).

Verified: ./gradlew test for RendererTest (15) + BlockTest (8) green; full 'just test' (clean check + detekt) BUILD SUCCESSFUL. Two compiler warnings (PlyReader.kt, GridStructuresTest.kt) are pre-existing and unrelated.
<!-- SECTION:NOTES:END -->
