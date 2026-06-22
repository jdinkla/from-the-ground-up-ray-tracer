---
id: TASK-25
title: >-
  Fix silent underfill in block-based renderers for small/non-divisible
  resolutions
status: In Progress
assignee:
  - '@claude'
created_date: '2026-06-22 14:20'
updated_date: '2026-06-22 15:20'
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
- [ ] #1 Block renderers fill every pixel for resolutions smaller than the block grid, or throw a clear typed exception consistent with ParallelRenderer
- [ ] #2 Non-divisible resolutions render the remainder rows/columns with no silently dropped pixels
- [ ] #3 Tests cover sub-block-grid and non-divisible resolutions for each block renderer; the cross-renderer output-equivalence test still passes
- [ ] #4 Update the TASK-7 RendererTest cases that currently pin the buggy underfill behavior to assert the corrected behavior
<!-- AC:END -->
