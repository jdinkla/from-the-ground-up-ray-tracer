---
id: TASK-25
title: >-
  Fix silent underfill in block-based renderers for small/non-divisible
  resolutions
status: In Progress
assignee:
  - '@claude'
created_date: '2026-06-22 14:20'
updated_date: '2026-06-22 15:22'
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
