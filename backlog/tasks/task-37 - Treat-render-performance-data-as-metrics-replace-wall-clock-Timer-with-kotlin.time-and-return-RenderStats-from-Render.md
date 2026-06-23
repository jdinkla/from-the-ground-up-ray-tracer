---
id: TASK-37
title: >-
  Treat render performance data as metrics: replace wall-clock Timer with
  kotlin.time and return RenderStats from Render
status: Done
assignee: []
created_date: '2026-06-23 12:15'
updated_date: '2026-06-23 12:23'
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
- [x] #1 Render.render(film, renderer) returns a RenderStats (monotonic Duration via kotlin.time + a snapshot of the Counter tallies) instead of logging; Counter is reset after each render exactly as before
- [x] #2 Render.render(worldDefinition, context) returns a RenderResult(film, world, stats); the stereo branch is timed too
- [x] #3 The hand-rolled wall-clock Timer (utilities/Timer.kt) and its test are removed; Grid build/subgrid timing uses kotlin.time (TimeSource.Monotonic), GridStructuresTest stays green
- [x] #4 Counter exposes snapshot(): Map<String,Int>; the stats()/printStats() presentation path is unchanged and CounterTest stays green
- [x] #5 The CLI (Main) logs the render duration and prints the counter stats, preserving the prior console output
- [x] #6 The Swing UI shows the render duration from RenderStats for the final Rendered/Saved-in lines instead of its own System.nanoTime measurement; live progress percentage/elapsed is unaffected
- [x] #7 A unit test covers Render.render(film, renderer) and Render.render(worldDefinition, context); ./gradlew build (tests + detekt) passes and a CLI render is verified manually
<!-- AC:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
Implemented A+B from the design discussion.

New value types (commonMain, world/RenderStats.kt): RenderStats(duration: kotlin.time.Duration, counts: Map<String,Int>) and RenderResult(film, world, stats).

Render.kt: render(film, renderer) now measures with kotlin.time.measureTime (monotonic) and returns RenderStats; render(worldDefinition, context) returns RenderResult and times the stereo branch with measureTimedValue. A private finishStats(duration) snapshots Counter and resets it, preserving the long-standing per-render reset semantics. Render no longer logs timing/counts (and the old duplicate "took X [ms]" line is gone) — measurement is decoupled from presentation.

Counter (jvmMain): added snapshot(): Map<String,Int>; stats()/printStats() presentation path unchanged (CounterTest green).

Presentation moved to callers: Main (CLI) logs "rendering took <ms> ms" and prints the counter stats via printStats; Swing shows stats.duration.inWholeMilliseconds for the final Rendered/Saved-in lines and no longer dumps Counter.stats to stdout. NOTE: the interactive Swing duration is now render-only (excludes world build), which is the more accurate render metric.

Timer: deleted the 2010 wall-clock Timer (korlibs DateTime.unixMillis) and TimerTest; Grid build/subgrid timing migrated to TimeSource.Monotonic.markNow()/measureTime (GridStructuresTest green).

Tests: new jvmTest world/RenderStatsTest covers both render overloads (duration >= 0, Counter snapshot captured, counter reset after render, RenderResult film resolution = context resolution).

Follow-ups noted but out of scope: JFR custom events / Micrometer for true exportable metrics; a Swing stats panel if the counter dump should be visible in the GUI again.
<!-- SECTION:NOTES:END -->

## Final Summary

<!-- SECTION:FINAL_SUMMARY:BEGIN -->
Render performance data is now a structured metric, not a log line. Render.render returns RenderStats/RenderResult (monotonic kotlin.time.Duration + a Counter snapshot) instead of logging; the CLI and Swing each present it (Swing reads the returned duration instead of re-measuring with System.nanoTime). Deleted the 2010 wall-clock Timer and migrated Grid to kotlin.time. Verified: ./gradlew build (tests + detekt) green, and a 720p CLI render still logs 'rendering took N ms' + the counter stats.
<!-- SECTION:FINAL_SUMMARY:END -->
