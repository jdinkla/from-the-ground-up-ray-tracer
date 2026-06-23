---
id: TASK-45
title: >-
  Surface render-time exceptions: propagate renderer worker-thread failures and
  show a modal error dialog in the Swing app
status: In Progress
assignee:
  - '@claude'
created_date: '2026-06-23 21:48'
updated_date: '2026-06-23 22:15'
labels:
  - renderer
  - swing
  - ui
  - bug
dependencies: []
priority: high
ordinal: 48000
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Repro: in the Swing app, select an area-light scene (e.g. World23.kt) and render it with an incompatible tracer (WHITTED instead of AREA). Every pixel shade throws java.lang.UnsupportedOperationException: 'AreaLight needs AreaLighting Tracer' (AreaLight.getDirection -> Phong.shade -> Whitted.trace). With the PARALLEL renderer these throws happen on the raw worker threads (ParallelRenderer.Worker.run, pixel loop at line ~99): the exception propagates out of run() so the worker DIES WITHOUT reaching barrier.await() (line ~107). The master thread (render(), barrier.await() at line ~38) then waits for parties that never arrive and HANGS on the CyclicBarrier, while the JVM prints 'Exception in thread Thread-N' to stderr. Because render(film) never returns (or never throws), the Swing render coroutine's catch(Exception) -> reportFailure(e) (which ALREADY shows a JOptionPane modal dialog) never fires, and the UI stays stuck 'Rendering...'. TASK-5 hardened the barrier's Interrupted/BrokenBarrier paths but NOT a worker throwing inside the pixel loop.\n\nFix (core, cover-first): ParallelRenderer workers must capture the first pixel-loop failure (e.g. AtomicReference<Throwable>), ALWAYS reach the barrier (try/finally) so the master is released instead of deadlocking, and the master must rethrow the captured failure so render(film) throws promptly with the original cause attached. Preserve TASK-34 cancellation (cancellation breaks the loop normally and still reaches the barrier; no failure recorded -> no throw). Verify the other multi-threaded renderers (ForkJoinRenderer, CoroutineBlockRenderer, NaiveCoroutineRenderer, VirtualThreadBlockRenderer) propagate a render-time throw to the caller of render(film); SequentialRenderer already does. Fix any that swallow.\n\nFix (UI): ensure the Swing render path surfaces the failure as a MODAL error dialog showing the meaningful root-cause message (reportFailure already shows a dialog; make sure the message is the real cause, walking the cause chain if the top exception message is null/generic), restores the idle UI (re-enable Render/PNG, stop the preview timer), and shows a failed status. The PNG export path should behave the same.\n\nOut of scope: making incompatible tracer+scene combinations actually render (the scene/tracer mismatch is user error); TASK-39's preferredTracer is only an audit hint and does not auto-select the tracer in the GUI.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Selecting an incompatible tracer for a scene (e.g. WHITTED on an area-light scene) no longer hangs or only prints to stderr: render(film) throws promptly with the original cause, and the renderer does not deadlock on the barrier
- [x] #2 ParallelRenderer captures the first worker pixel-loop failure, always releases the master via the barrier (no deadlock), and rethrows it; TASK-34 cancellation still works (no spurious failure on cancel)
- [x] #3 The other multi-threaded renderers (ForkJoin, both coroutine variants, virtual-threads) are verified to propagate a render-time exception to the caller; any that swallow are fixed
- [x] #4 Cover-first frozen tests: a renderer driven with a single-ray renderer that throws makes render(film) throw (not hang, not swallow) for ParallelRenderer and the other multi-threaded strategies
- [ ] #5 The Swing app shows a MODAL error dialog with the real cause message when a render (or PNG export) fails, then returns to the idle UI (Render/PNG re-enabled, preview timer stopped, failed status shown)
- [x] #6 detekt and the full ./gradlew clean check stay green
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Cover-first: add ThrowingSingleRayRenderer fake (throws on render(r,c) after N pixels) to RendererTest; add 'render(film) throws' tests for Parallel, ForkJoin, a coroutine variant, NaiveCoroutine, VirtualThread. Confirm Parallel hangs + VirtualThread swallows against current code (bounded run / reasoning).
2. Fix ParallelRenderer: AtomicReference<Throwable> failure field; worker wraps pixel loop in try/catch(Throwable) storing first failure via compareAndSet, try/FINALLY always reaches barrier.await(); cancellation break = normal exit, no failure. Master after barrier.await() rethrows captured failure (original message/cause preserved). Drop barrier.reset() worker path in favour of always-reach-barrier.
3. Fix VirtualThreadBlockRenderer: capture first worker failure in AtomicReference, join all, then rethrow (Thread.join swallows otherwise).
4. Confirm ForkJoin/Coroutine/NaiveCoroutine propagate (structured concurrency / RecursiveAction.join rethrow) — tests assert this; no code change expected.
5. Swing: reportFailure walks cause chain to root cause for dialog/status message (handle null/blank top message); apply to png() path too (already uses reportFailure). JaCoCo-excluded -> manual verify.
6. Verify: gradlew clean check green; CLI fail-fast with World23.kt + WHITTED + PARALLEL no longer hangs; gradlew swing launches cleanly.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
Cover-first confirmed against UNFIXED code via a bounded standalone probe (4s watchdog) compiled with kotlin 2.3.0 compiler-embeddable: Parallel=HANG (deadlock on barrier), VirtualThread=RETURNED-NORMALLY (swallowed), ForkJoin/CoroutineBlock/NaiveCoroutine=THREW (already propagated). After fixes all five = THREW. Cancellation probe: Parallel/VirtualThread return normally with no spurious throw (stopped early). Root-cause message 'AreaLight needs AreaLighting Tracer' survives the IllegalStateException wrapper.

Renderer core changes:
- ParallelRenderer.kt: added AtomicReference<Throwable> workerFailure; Worker.run wraps pixel loop (renderRows) in try/catch(Throwable)->compareAndSet(null,e), try/FINALLY always reaches the barrier (awaitBarrier), so the master is released instead of deadlocking. Master rethrows the captured failure (wrapped in IllegalStateException with original cause) AFTER barrier.await() returns. Cancellation breaks the loop normally -> records nothing -> master returns without throwing. Removed dead 'count' var. Kept the existing barrier.reset() only for the worker's own interrupt path.
- VirtualThreadBlockRenderer.kt: Thread.join() does NOT rethrow the thread-body exception, so it swallowed failures. Added AtomicReference<Throwable> captured per worker (compareAndSet first-wins); master() joins all then rethrows failure.get().
- ForkJoin/CoroutineBlock/NaiveCoroutine: already propagate (RecursiveAction.join rethrow / structured concurrency) -> no code change; tests pin it.

Tests (src/jvmTest/.../renderer/RendererTest.kt): added ThrowingSingleRayRenderer fake (throws after N shades, atomic counter); 5 'surfaces a worker render-time failure' shouldThrow tests (one per multi-threaded strategy), a 'preserves the original failure message' test (rootCauseMessageOf walks the chain), and a 'does not report a spurious failure when cancelled' test for Parallel.

VERIFICATION:
- ./gradlew clean check GREEN (compile + 28 RendererTest tests incl. 7 new, all suites, detekt). Two pre-existing 'Unchecked cast' warnings (PlyReader.kt, GridStructuresTest.kt) are unrelated to this change. No detekt suppressions added to dodge findings: ParallelRenderer.render's ThrowsCount was resolved by extracting awaitWorkers()/rethrowWorkerFailure() (genuine cohesion split), not @Suppress.
- CLI fail-fast (was hanging before fix): ./gradlew run --args='--world=World23.kt --tracer=WHITTED --renderer=PARALLEL --resolution=480p' now terminates non-zero with: main thread 'IllegalStateException: ParallelRenderer aborted: a worker failed while rendering resolution Resolution(width=853, height=480)' Caused by 'UnsupportedOperationException: AreaLight needs AreaLighting Tracer' (full chain Whitted.trace->Phong.shade->AreaLight.getDirection visible). Exit value 1, no hang.
- ./gradlew swing launches with no startup/EDT exception (ran ~18s, log clean, GUI up).

SWING (JaCoCo-excluded, manual-verify only): reportFailure now computes rootCauseMessage(e) walking the cause chain to the deepest non-blank message, prefixed with the root exception simple class name (e.g. 'UnsupportedOperationException: AreaLight needs AreaLighting Tracer'), used for both the status bar and the modal JOptionPane.ERROR_MESSAGE dialog. Both render() and png() paths route failures through reportFailure and their finally blocks restore the idle UI (stop preview timer / clear indeterminate progress, setBusy(false) re-enables Render+PNG) and set a Failed status. The actual modal-dialog appearance during an interactive failing render needs a human at the display (residual manual check).

POST-REVIEW HARDENING (deadlock-regression fail-fast): the new worker-failure tests now bound their runtime so a *reintroduced* deadlock fails fast instead of hanging the suite.

Investigated the coordinator's suggested Kotest .config(timeout=30.seconds) and found it INSUFFICIENT on its own for this failure mode: a thread parked in CyclicBarrier.await() is not interruptible by coroutine cancellation, so when I reintroduced the pre-fix deadlock the Kotest timeout fired but the parked non-daemon master/worker threads kept the test JVM alive and the whole suite still hung (verified: 2min wall-clock, no completion).

Robust fix implemented instead:
- RendererTest: captureRenderFailure() drives renderer.render(film) on a DAEMON thread and join()s it for failDeadline (30s, ~ms real runtime). If the driver is still alive after the deadline the test FAILS with 'render() did not return within 30s — the renderer deadlocked'. Returns the thrown Throwable so callers assert on it (Parallel tightened to shouldBeInstanceOf<IllegalStateException>; message-preservation test asserts root cause). Kept .config(timeout = failDeadline) as a coarser backstop.
- ParallelRenderer: render workers are now started as DAEMON threads (Thread(aWorker).apply { isDaemon = true }) so a leaked/stuck worker can never keep the JVM or a CI test process alive. VirtualThreadBlockRenderer's workers are virtual threads (always daemon) — no change needed.

Verified by re-running the regression experiment with the deadlock reintroduced and a short 3s deadline: both Parallel worker-failure tests FAILED FAST, all 28 tests completed, suite finished in ~9s (BUILD FAILED, not hung). Reverted the experimental breakage and restored failDeadline=30s.

Final: ./gradlew clean check GREEN, suite completes in ~22s, RendererTest 28 tests / 0 failures. detekt clean (no new findings, no suppressions). Note: making ParallelRenderer workers daemon is a small production change slightly beyond the original wording but directly serves this task's failure-mode hardening and is strictly safer; existing rendering-behavior tests (coverage/equivalence/cancellation) stay green unchanged.
<!-- SECTION:NOTES:END -->
