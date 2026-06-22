---
id: TASK-5
title: Harden renderer threading error handling
status: Done
assignee:
  - '@claude'
created_date: '2026-06-22 09:11'
updated_date: '2026-06-22 11:16'
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

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
Approach: scoped strictly to the two files named in the ACs (ParallelRenderer.kt, Polynomials.kt). Inventoried the sibling renderers in jvmMain/renderer (ForkJoin, CoroutineBlock, NaiveCoroutine, VirtualThreadBlock) -- none have the swallow-and-printStackTrace pattern; they already propagate failures (coroutine scopes via runBlocking/coroutineScope, ForkJoin via RecursiveAction.join, VirtualThread via Thread.join). No change needed there.

ParallelRenderer.kt:
- createWorkers: generic RuntimeException("viewPlane.vres % numThreads != 0") -> IllegalArgumentException with contextual message (height, numThreads/4 divisor).
- render() main-thread barrier.await: removed both printStackTrace swallows. InterruptedException -> restore interrupt flag (Thread.currentThread().interrupt()) then rethrow as IllegalStateException(cause preserved) with resolution context. BrokenBarrierException -> rethrow as IllegalStateException(cause preserved): a worker failed, so the render is incomplete and must not return silently.
- Worker.run barrier.await: InterruptedException -> restore interrupt flag and barrier.reset() so the master thread is released with BrokenBarrierException (and then throws) instead of hanging forever. BrokenBarrierException -> Logger.warn (not silent printStackTrace); the master observes the same broken barrier and reports the failure. (Logger has no Throwable overload; folded the exception into the message string rather than expand the Logger API, which is out of scope.)

Polynomials.kt: the three bare AssertionError() (solveCubic/solveQuadric/solveQuartic array-size guards) -> require(...) which throws IllegalArgumentException with a descriptive message naming the solver and the expected sizes. Happy path unchanged (predicate is the inverse of the old guard condition).

Tests:
- PolynomialsTest: added 3 cover-first tests pinning that wrongly-sized arrays throw IllegalArgumentException with a message containing the solver name (solveQuadric/solveCubic/solveQuartic). Existing happy-path tests unchanged and still green.
- RendererTest: tightened 'parallel renderer fails on incompatible resolution' to assert the specific IllegalArgumentException type + contextual message ('ParallelRenderer', 'height'). The 'fills all pixels' happy-path test is unchanged and still green.

Verification: ./gradlew test for PolynomialsTest+RendererTest green; full 'just test' (clean check + detekt) GREEN. Two pre-existing unchecked-cast compiler warnings (PlyReader.kt, GridStructuresTest.kt) are unrelated to this change.

Not unit-tested (noted explicitly): the threading-failure paths (worker InterruptedException -> barrier.reset -> master BrokenBarrierException rethrow) are not deterministically triggerable without injecting an interrupt mid-render and racing the barrier; per specs/testing.md determinism rules I did not contort a flaky test for them. The deterministic, testable surfaces (resolution guard exception type/message, Polynomials guard exception type/message) are covered.
<!-- SECTION:NOTES:END -->

## Final Summary

<!-- SECTION:FINAL_SUMMARY:BEGIN -->
Hardened renderer threading error handling. ParallelRenderer no longer swallows threading failures via printStackTrace: main-thread InterruptedException restores the interrupt flag and rethrows wrapped in IllegalStateException (cause preserved, resolution context in message); main-thread BrokenBarrierException rethrows wrapped; worker InterruptedException restores the interrupt flag and resets the CyclicBarrier so the master is released (cannot deadlock) and reports the failure; worker BrokenBarrierException is logged via Logger.warn (master already reports). The generic RuntimeException became IllegalArgumentException naming the renderer/height/numThreads. Polynomials.kt's three bare AssertionError() guards became require(...) (IllegalArgumentException) with solver-name + expected-size messages; reviewer confirmed the require predicates are exact De Morgan inverses of the originals so happy-path behaviour is unchanged. Sibling renderers (ForkJoin, CoroutineBlock, NaiveCoroutine, VirtualThreadBlock) were inventoried — none share the swallow pattern, all already propagate. Cover-first: added 3 PolynomialsTest guard tests; tightened the RendererTest bad-resolution test to assert the new specific type+message (legitimate behaviour change). Non-deterministic threading-failure paths are not unit-tested per specs/testing.md determinism rules (cannot trigger a mid-render interrupt/barrier race deterministically); verified by reasoning + reviewer trace. Verified via just test (clean check + detekt) BUILD SUCCESSFUL. Committed as d2c76ca.
<!-- SECTION:FINAL_SUMMARY:END -->
