---
id: TASK-17
title: Load scene DSL files at runtime (ray-tracer FILE.scene.kts)
status: In Progress
assignee:
  - '@claude'
created_date: '2026-06-22 09:21'
updated_date: '2026-06-22 16:31'
labels:
  - enhancement
dependencies: []
priority: low
ordinal: 17000
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
OPTIONAL / nice-to-have. Today scenes are Kotlin objects implementing WorldDefinition, compiled into the jar and auto-discovered via classgraph (Worlds.kt). Goal: also allow rendering a scene authored in the existing Kotlin DSL from an external file at runtime, e.g. `ray-tracer --world=mountain.scene.kts`, without rebuilding.

Recommended approach: embed the Kotlin scripting host (kotlin-scripting-jvm-host, Kotlin 2.3.0) with a custom script definition whose implicitReceiver is WorldScope plus defaultImports. That lets a scene file be the bare DSL body (camera/lights/materials/objects) — identical to what's inside Builder.build { } today — with no wrapper or imports. A FileWorldDefinition(path) implementing WorldDefinition evaluates the script (fresh WorldScope, implicitReceivers(scope)) and returns scope.world. Wire it in by having worldDef()/the CLI resolve an existing filesystem path before falling back to the classgraph worldMap, so the rest of Render.render is unchanged.

Tradeoffs to accept/note: fat jar grows ~tens of MB (embedded compiler); first-run compile latency ~1-2s (cache compiled scripts by file hash if needed); scene files execute arbitrary code (fine for a local tool, don't run untrusted files). Alternatives considered: JSR-223 (less host code but scene files need explicit Builder.build wrapper + imports); a JSON/YAML data format (lightweight + sandboxed but abandons the Kotlin DSL — rejected since keeping the DSL is the point).
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 ray-tracer can render a scene from an external file written in the existing Kotlin DSL, passed as a file path, without recompiling the jar
- [ ] #2 External scene files reuse the existing WorldScope DSL surface unchanged; no changes required to Builder/WorldScope to support the common cases
- [ ] #3 When the --world argument is an existing filesystem path it loads via the script host; otherwise it falls back to the classgraph worldMap (existing behavior unmodified)
- [ ] #4 Script compilation/evaluation errors are surfaced with the compiler diagnostics (file + message), not a silent failure or bare stack trace
- [ ] #5 At least one sample .scene.kts file and a test covering load-from-file are added
<!-- AC:END -->
