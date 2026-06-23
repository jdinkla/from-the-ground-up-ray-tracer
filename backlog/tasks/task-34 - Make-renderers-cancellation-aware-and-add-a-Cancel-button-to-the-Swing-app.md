---
id: TASK-34
title: Make renderers cancellation-aware and add a Cancel button to the Swing app
status: In Progress
assignee:
  - '@claude'
created_date: '2026-06-22 21:58'
updated_date: '2026-06-23 20:41'
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
