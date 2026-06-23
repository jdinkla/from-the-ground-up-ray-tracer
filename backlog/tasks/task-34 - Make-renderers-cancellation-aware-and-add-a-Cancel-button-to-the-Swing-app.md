---
id: TASK-34
title: Make renderers cancellation-aware and add a Cancel button to the Swing app
status: In Progress
assignee:
  - '@claude'
created_date: '2026-06-22 21:58'
updated_date: '2026-06-23 20:48'
labels:
  - swing
  - ui
  - renderer
dependencies: []
priority: medium
ordinal: 37000
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
IRenderer.render(film) is a blocking call and no renderer (ParallelRenderer raw threads + CyclicBarrier, CoroutineBlockRenderer's own runBlocking, SequentialRenderer tight loop, ForkJoin, virtual threads) checks for cancellation inside its pixel loop, so a long render cannot be stopped. Add cooperative cancellation to the renderer core, then wire a Cancel button into the Swing app that actually stops in-flight CPU work and restores the UI. This is split out of TASK-33 because honest cancellation requires changing the tested renderer core (cover-first), unlike the pure-UI improvements there.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 IRenderer (or a renderer-side mechanism) supports cooperative cancellation; an in-progress render stops promptly when cancelled and does not keep consuming CPU after cancellation
- [ ] #2 Cancellation behavior is covered by tests for the renderer strategies, written cover-first and then frozen
- [ ] #3 The Swing app shows a Cancel button during a render that stops the in-flight render and restores the UI to idle (re-enables Render/PNG)
- [ ] #4 detekt and the full build stay green
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Add CancellationToken interface + NoCancellation no-op + AtomicCancellationToken (java.util.concurrent.atomic) in commonMain/renderer. 2. Change IRenderer.render(film) -> render(film, cancellation: CancellationToken = NoCancellation) so all existing callers/tests are unaffected (default arg). 3. Have each renderer check the token at coarse granularity and stop early: SequentialRenderer per-row; ForkJoin/Coroutine/VirtualThread per-block (and skip work in already-started blocks); ParallelRenderer per-row in Worker; NaiveCoroutine per-row of launches. 4. Cover-first: add cancellation tests in RendererTest (sequential, fork-join, parallel, a coroutine variant, virtual threads) that flip the token from an instrumented film after N pixels and assert the render returns early without writing all pixels. Confirm tests FAIL without the checks, then add checks -> green & freeze. 5. Wire Swing Cancel button: add a JButton('Cancel'), keep an AtomicCancellationToken + Job per render, on click set token + cancel job, restore idle UI. Pass token through Render.render(film, renderer, cancellation). 6. ./gradlew build green (detekt + tests).
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
Implemented cooperative cancellation via a CancellationToken seam.

Mechanism (chosen for uniformity across thread- AND coroutine-based renderers): new commonMain/renderer/CancellationToken.kt — interface CancellationToken { isCancelled }, object NoCancellation (no-op, false), class AtomicCancellationToken (AtomicBoolean-backed, settable from any thread). IRenderer.render now takes render(film, cancellation: CancellationToken = NoCancellation) — a default arg, so every existing caller/test is byte-for-byte unaffected and there is no perf regression on the uncancelled path. Coroutine cancellation alone was rejected: the raw-thread / fork-join / virtual-thread renderers and CoroutineBlockRenderer's own runBlocking would not honour it, and the Swing render call is a synchronous blocking renderer.render() so job.cancel() cannot interrupt the CPU loop — the token can.

Each renderer polls the token at COARSE granularity (per row / per block, never per pixel): SequentialRenderer (per row), ForkJoinRenderer.Worker + CoroutineBlockRenderer.work + VirtualThreadBlockRenderer.work (per row of a block, so in-flight blocks bail and unstarted blocks skip), ParallelRenderer.Worker (per row, still reaches the barrier so the master is released cleanly), NaiveCoroutineRenderer (per row before launching that row's coroutines).

Render.render(film, renderer, cancellation = NoCancellation) threads the token through.

Cover-first (AC#2): added a CancellingFilm fake (flips the token after N writes, counts all writes with AtomicInteger) and 5 'stops early when cancelled partway' tests in RendererTest covering sequential, fork-join, parallel-threads, coroutine-block, and virtual-threads on a 256x256 film cancelled after 64 px, asserting pixelsWritten < total. Confirmed all 5 FAILED before the token checks were added (renderers ignored the token and wrote all 65536 px) and PASS after — then frozen. Existing 15 RendererTest tests unchanged and green; updated the IRenderer test fakes in ContextTest/RenderStatsTest to the new signature (mechanical, assertions unchanged).

Files changed: commonMain/renderer/CancellationToken.kt (new), commonMain/renderer/IRenderer.kt, commonMain/renderer/SequentialRenderer.kt, commonMain/world/Render.kt, jvmMain/renderer/{ForkJoin,Parallel,CoroutineBlock,NaiveCoroutine,VirtualThreadBlock}Renderer.kt, jvmMain/ui/swing/FromTheGroundUpRayTracer.kt, jvmTest/renderer/RendererTest.kt, commonTest/world/ContextTest.kt, jvmTest/world/RenderStatsTest.kt.

Swing wiring (AC#3): added a Cancel JButton (disabled when idle, enabled only for the interactive render — the PNG path has no mid-render stop so its button stays disabled). render() now creates an AtomicCancellationToken, stores it + the launch Job in fields, passes the token to Render.render, and shows 'Cancelled <scene>' when token.isCancelled. onCancel() flips the token (stops CPU work promptly) and cancels the job (unwinds the coroutine); the coroutine's finally (in NonCancellable+Swing) stops the preview Timer and calls setBusy(false), which re-enables Render/PNG, disables Cancel, and clears the token/job. Verified manually: ./gradlew swing launches, the JVM reaches the EDT and shows the frame with no exception (JaCoCo-excluded zone, not unit-tested by design).

Full check: ./gradlew clean check (compile + all tests + detekt) BUILD SUCCESSFUL.
<!-- SECTION:NOTES:END -->
