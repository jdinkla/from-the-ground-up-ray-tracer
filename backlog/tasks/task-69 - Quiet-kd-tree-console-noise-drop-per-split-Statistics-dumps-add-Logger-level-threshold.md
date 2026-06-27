---
id: TASK-69
title: >-
  Quiet kd-tree console noise: drop per-split/Statistics dumps, add Logger level
  threshold
status: Done
assignee: []
created_date: '2026-06-27 05:33'
updated_date: '2026-06-27 05:36'
labels: []
dependencies: []
ordinal: 74000
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Building a kd-tree scene (e.g. BunnyRowKdTree) floods the console: SpatialMedianBuilder/Simple2Builder logged one INFO 'Splitting ...' line per inner node (~13k for the bunny), KDTree.initialize() unconditionally dumped Statistics.print() via raw println (unstructured, no level), and it emitted a 'Ideal maxDepth = N, but set to M' WARN per tree. The Logger had levels but no threshold, so nothing could be filtered. This quiets the default console while keeping the diagnostics recoverable.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 The per-inner-node 'Splitting ...' log is gone from SpatialMedianBuilder and Simple2Builder (imports/comments updated, no unused-import warnings under allWarningsAsErrors)
- [x] #2 KDTree.initialize() no longer auto-dumps Statistics.print(); the Statistics class and Histogram.println() are retained and still tested
- [x] #3 Logger has a configurable level threshold (default INFO) that suppresses messages below it; writeLogMessage gates on it
- [x] #4 The 'Ideal maxDepth' message is demoted from warn to debug, so it is hidden by default
- [x] #5 A LoggerTest covers the threshold (default hides DEBUG/shows INFO, at/above prints, below suppressed) and restores global state between tests
- [x] #6 ./gradlew build is green (compile + detekt + tests); existing CounterTest/HistogramTest/KDTreeTest still pass
- [x] #7 Manually verified: running BunnyRowKdTree emits no Splitting/Statistics/maxDepth lines on the default console
<!-- AC:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
Console noise from kd-tree scene builds removed in three steps:

1. Per-split flood: deleted the per-inner-node Logger.info("Splitting ...") in SpatialMedianBuilder.kt and Simple2Builder.kt (~13k lines for BunnyRowKdTree), plus their now-unused Logger imports and the stale comments/KDoc that referenced the log message.
2. Unstructured Statistics dump: removed the unconditional Statistics.print(this) call from KDTree.initialize(). The Statistics class and Histogram.println() are retained (still covered by StatisticsTest/HistogramTest) for deliberate diagnostics.
3. Per-tree 'Ideal maxDepth' line: added a configurable level threshold to Logger (var threshold, default LogLevel.INFO; writeLogMessage now drops anything below it) and demoted the message from warn to debug, so it is hidden by default but recoverable by setting Logger.threshold = DEBUG. As a side effect the DEBUG 'PLY: N faces to read' lines are also hidden by default.

Tests: new LoggerTest (commonTest, 5 cases, saves/restores the global threshold per spec/testing.md §9) covers default-hides-DEBUG/shows-INFO, at/above prints, below suppressed, and the level tag. Updated two now-stale KDTreeTest comments (assertions unchanged/frozen).

Verification: ./gradlew build green (compile + detekt + tests under allWarningsAsErrors). CounterTest still green (Counter.stats uses Logger.info, shown at the INFO default). Ran BunnyRowKdTree.kt at 720p: 0 Splitting/Statistics/Ideal-maxDepth lines on the console; only the useful INFO banner (renderer/tracer/resolution/'render starts') remains.

Follow-up (not done, optional): no runtime toggle yet -- Logger.threshold is only settable programmatically. A --log-level CLI option (Clikt) or a system property read in Main would let users flip to DEBUG without recompiling.
<!-- SECTION:NOTES:END -->
