---
id: TASK-5
title: Harden renderer threading error handling
status: In Progress
assignee:
  - '@claude'
created_date: '2026-06-22 09:11'
updated_date: '2026-06-22 11:13'
labels:
  - reliability
  - concurrency
dependencies: []
references:
  - docs/arc42/11_risks_and_technical_debt.md
priority: high
ordinal: 5000
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
ParallelRenderer.kt catches InterruptedException and BrokenBarrierException but only prints a stack trace, so parallel-render failures fail silently. It also throws a generic RuntimeException with a cryptic message, and Polynomials.kt throws a bare AssertionError. Propagate or recover from threading failures and use specific exception types with descriptive messages across the renderer strategies.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Threading exceptions are propagated or recovered, never silently swallowed
- [x] #2 Render failures surface a clear, contextual error (no bare printStackTrace)
- [x] #3 Generic RuntimeException/AssertionError replaced with specific typed exceptions carrying messages
<!-- AC:END -->



## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Cover-first: confirm RendererTest pins ParallelRenderer behavior (fills pixels; bad resolution throws). Tighten the bad-resolution test to assert the new specific exception type + message. Confirm PolynomialsTest pins happy-path; add tests that illegal array sizes throw the new specific exception (IllegalArgumentException with descriptive message) for solveQuadric/solveCubic/solveQuartic.
2. ParallelRenderer: replace generic RuntimeException("viewPlane.vres % numThreads != 0") with IllegalArgumentException carrying contextual message (resolution, numThreads). Replace the two printStackTrace swallows on the main-thread barrier.await with: restore interrupt flag on InterruptedException and rethrow wrapped in IllegalStateException (RenderException) preserving cause; wrap BrokenBarrierException likewise. In Worker.run, restore interrupt flag and propagate by breaking the barrier so the master await sees BrokenBarrierException instead of hanging silently.
3. Polynomials: replace the three bare AssertionError() with IllegalArgumentException carrying which solver + expected array sizes.
4. Inventory siblings (ForkJoin/Coroutine/VirtualThread): confirmed they propagate exceptions (no swallow). No change needed; note in report.
5. Run ./gradlew test for the two affected classes, then full 'just test'.
<!-- SECTION:PLAN:END -->
