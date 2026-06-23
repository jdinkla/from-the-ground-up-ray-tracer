---
id: TASK-45
title: >-
  Surface render-time exceptions: propagate renderer worker-thread failures and
  show a modal error dialog in the Swing app
status: To Do
assignee: []
created_date: '2026-06-23 21:48'
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
- [ ] #1 Selecting an incompatible tracer for a scene (e.g. WHITTED on an area-light scene) no longer hangs or only prints to stderr: render(film) throws promptly with the original cause, and the renderer does not deadlock on the barrier
- [ ] #2 ParallelRenderer captures the first worker pixel-loop failure, always releases the master via the barrier (no deadlock), and rethrows it; TASK-34 cancellation still works (no spurious failure on cancel)
- [ ] #3 The other multi-threaded renderers (ForkJoin, both coroutine variants, virtual-threads) are verified to propagate a render-time exception to the caller; any that swallow are fixed
- [ ] #4 Cover-first frozen tests: a renderer driven with a single-ray renderer that throws makes render(film) throw (not hang, not swallow) for ParallelRenderer and the other multi-threaded strategies
- [ ] #5 The Swing app shows a MODAL error dialog with the real cause message when a render (or PNG export) fails, then returns to the idle UI (Render/PNG re-enabled, preview timer stopped, failed status shown)
- [ ] #6 detekt and the full ./gradlew clean check stay green
<!-- AC:END -->
