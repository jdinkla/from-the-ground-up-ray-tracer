---
id: TASK-34
title: Make renderers cancellation-aware and add a Cancel button to the Swing app
status: In Progress
assignee:
  - '@claude'
created_date: '2026-06-22 21:58'
updated_date: '2026-06-23 20:39'
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
