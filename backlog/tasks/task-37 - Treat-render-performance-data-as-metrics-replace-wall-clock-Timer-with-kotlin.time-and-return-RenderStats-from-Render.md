---
id: TASK-37
title: >-
  Treat render performance data as metrics: replace wall-clock Timer with
  kotlin.time and return RenderStats from Render
status: To Do
assignee: []
created_date: '2026-06-23 12:15'
labels: []
dependencies: []
ordinal: 40000
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Render timing is emitted as free-text log lines from Render.render (a metric formatted as a log), measured with a 2010-era hand-rolled Timer that uses the wall clock (korlibs DateTime.now().unixMillis, millisecond resolution) instead of a monotonic clock. Because the value is logged rather than returned, the Swing UI cannot reach it and re-measures the same render independently with System.nanoTime. Decouple measurement from presentation: Render computes structured stats (monotonic duration + a snapshot of the Counter tallies) and returns them; each caller (CLI, Swing, tests) decides how to present. Scope is A+B from the design discussion (kotlin.time + RenderStats); JFR/Micrometer are noted follow-ups, not in scope.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Render.render(film, renderer) returns a RenderStats (monotonic Duration via kotlin.time + a snapshot of the Counter tallies) instead of logging; Counter is reset after each render exactly as before
- [ ] #2 Render.render(worldDefinition, context) returns a RenderResult(film, world, stats); the stereo branch is timed too
- [ ] #3 The hand-rolled wall-clock Timer (utilities/Timer.kt) and its test are removed; Grid build/subgrid timing uses kotlin.time (TimeSource.Monotonic), GridStructuresTest stays green
- [ ] #4 Counter exposes snapshot(): Map<String,Int>; the stats()/printStats() presentation path is unchanged and CounterTest stays green
- [ ] #5 The CLI (Main) logs the render duration and prints the counter stats, preserving the prior console output
- [ ] #6 The Swing UI shows the render duration from RenderStats for the final Rendered/Saved-in lines instead of its own System.nanoTime measurement; live progress percentage/elapsed is unaffected
- [ ] #7 A unit test covers Render.render(film, renderer) and Render.render(worldDefinition, context); ./gradlew build (tests + detekt) passes and a CLI render is verified manually
<!-- AC:END -->
