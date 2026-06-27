---
id: TASK-70
title: 'Replace hand-rolled Logger with Kermit (thin facade), drop dead logback dep'
status: Done
assignee: []
created_date: '2026-06-27 06:03'
updated_date: '2026-06-27 06:06'
labels: []
dependencies: []
ordinal: 75000
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
The utilities.Logger was a hand-rolled object (println + korlibs timestamp) written to stay in commonMain without a JVM-only logging dependency. That rationale has lapsed: the project is JVM-only now and already declared ch.qos.logback:logback-classic, which is completely unused (no SLF4J usage anywhere, no config; slf4j-api is pulled only by logback itself). Adopt Kermit (co.touchlab:kermit), a Kotlin-native multiplatform logger, behind a thin facade so the ~54 call sites stay 'Logger.info(...)'. A custom LogWriter preserves the existing 'TIMESTAMP LEVEL message' console format. Follows TASK-69 (which silenced kd-tree console noise and added the level threshold).
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 co.touchlab:kermit is added (pinned via refreshVersions); the unused ch.qos.logback:logback-classic dependency and its versions.properties entry are removed
- [x] #2 utilities.Logger is a thin facade delegating to Kermit's global logger; its public API (LogLevel, threshold, debug/info/warn/error) and all ~54 call sites are unchanged
- [x] #3 A custom Kermit LogWriter reproduces the 'TIMESTAMP LEVEL message' format (DEBUG/INFO/WARN/ERROR labels), printed to stdout
- [x] #4 threshold still gates output (default INFO hides DEBUG) by delegating to Kermit's minimum severity; LoggerTest passes unchanged
- [x] #5 ./gradlew build is green (compile + detekt + tests under allWarningsAsErrors); CounterTest still passes (Logger.info output format preserved)
- [x] #6 Manually verified: running BunnyRowKdTree shows the same TIMESTAMP INFO banner and no SLF4J 'no provider' warning on stderr
<!-- AC:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
Migrated utilities.Logger from a hand-rolled println logger to Kermit (co.touchlab:kermit 2.0.6) behind a thin facade.

Build:
- build.gradle.kts: removed the unused implementation('ch.qos.logback:logback-classic:_'), added implementation('co.touchlab:kermit:_'). Verified slf4j-api was pulled ONLY by logback (nothing else uses SLF4J), so removal is safe and introduces no 'no provider' warning.
- versions.properties: refreshVersions pinned it as the short key version.kermit=2.0.6 (its built-in rule); removed my redundant version.co.touchlab..kermit line and the dead logback block. Runtime classpath now resolves co.touchlab:kermit-jvm:2.0.6 with no slf4j/logback.

Code (Logger.kt):
- Logger stays an object with the same public API (LogLevel, threshold, debug/info/warn/error) so all ~54 call sites and 15 imports are untouched.
- It delegates to Kermit's global logger (Kermit.d/i/w/e); threshold maps to Kermit.setMinSeverity (default INFO, so DEBUG stays hidden).
- A private TimestampLogWriter : LogWriter() reproduces the prior 'TIMESTAMP LEVEL message' format (project labels DEBUG/INFO/WARN/ERROR, korlibs DateTime/FORMAT2), printed to stdout; appends a Throwable stacktrace when present.

Tests: LoggerTest (5) and CounterTest (8) pass unchanged — the facade preserves both the threshold gating and the stdout line format CounterTest parses.

Verification:
- ./gradlew build green (compile + detekt + tests under allWarningsAsErrors).
- Ran BunnyRowKdTree.kt 720p: console banner identical to before ('2026-...Z INFO ...'), 0 SLF4J warnings on stderr, 0 kd-tree noise markers (TASK-69 silencing intact).
<!-- SECTION:NOTES:END -->
