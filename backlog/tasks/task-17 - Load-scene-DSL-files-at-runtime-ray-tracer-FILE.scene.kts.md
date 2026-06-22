---
id: TASK-17
title: Load scene DSL files at runtime (ray-tracer FILE.scene.kts)
status: In Progress
assignee:
  - '@claude'
created_date: '2026-06-22 09:21'
updated_date: '2026-06-22 16:42'
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
- [x] #1 ray-tracer can render a scene from an external file written in the existing Kotlin DSL, passed as a file path, without recompiling the jar
- [x] #2 External scene files reuse the existing WorldScope DSL surface unchanged; no changes required to Builder/WorldScope to support the common cases
- [x] #3 When the --world argument is an existing filesystem path it loads via the script host; otherwise it falls back to the classgraph worldMap (existing behavior unmodified)
- [x] #4 Script compilation/evaluation errors are surfaced with the compiler diagnostics (file + message), not a silent failure or bare stack trace
- [x] #5 At least one sample .scene.kts file and a test covering load-from-file are added
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Add Kotlin scripting-host deps (scripting-jvm-host, scripting-jvm, scripting-common) as implementation in build.gradle.kts; confirm resolve + build.
2. jvmMain: SceneScript template (@KotlinScript, .scene.kts) with ScriptCompilationConfiguration: implicitReceivers(WorldScope), defaultImports(DSL types) so the body is the bare Builder.build{} DSL.
3. jvmMain: FileWorldDefinition(path): WorldDefinition; id=file name; world() evals script vs fresh WorldScope (implicitReceivers(scope)), returns scope.world. Surface ResultWithDiagnostics failures via typed SceneScriptException (file + each diagnostic msg + line) (AC#4).
4. Resolution seam: jvmMain SceneResolver.resolveWorld(id) -> existing file path => FileWorldDefinition else requireWorldDef(id, worldMap). Add resolver param to Render.render(String,...) defaulting to existing worldMap behavior; Main passes the file-first resolver. requireWorldDef stays pure (AC#3, preserves fail-fast).
5. Reconcile TASK-15: relax CommandLine --world from .choice() to a plain option with a Clikt validate that accepts existing-file OR known worldMap id, else fails fast listing scenes. Update CommandLine/Main wiring + tests.
6. Sample: add scenes/Sample.scene.kts (simple sphere/Whitted) for users; add test-resources copy for the jvmTest.
7. jvmTest (Kotest StringSpec): FileWorldDefinition(path).world() => asserts World contents (camera/objects/materials); broken scene => SceneScriptException with file+message; SceneResolver: existing path => FileWorldDefinition, unknown non-file id => fails fast, known worldMap id => existing def.
8. just test green + detekt clean (no baseline). Manual: gradlew run --world=<sample> renders PNG; unknown non-file id fails fast; worldMap id still renders. Measure fat-jar size delta + first-run latency.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
Implemented: build.gradle.kts adds kotlin scripting-common/jvm/jvm-host (resolve to 2.3.0). SceneScript (@KotlinScript .scene.kts) with implicitReceivers(WorldScope) + defaultImports(Color/Point3D/Normal/Vector3D) + dependenciesFromCurrentContext(wholeClasspath). FileWorldDefinition(path) evals via BasicJvmScriptingHost against a fresh WorldScope, returns scope.world; failures -> typed SceneScriptException(file, diagnostics) with file+severity+line+message. SceneResolver.resolveWorld(id): existing file -> FileWorldDefinition else requireWorldDef(id, worldMap). Render.render(String,...) gained resolveWorld param (default ::requireWorldDef = unchanged worldMap behavior); Main passes SceneResolver. CommandLine --world relaxed from .choice() to a default+validate that accepts known id OR existing file, else fails fast listing scenes; pure predicate isAcceptableWorldArg(value, ids, fileExists). Sample at scenes/Sample.scene.kts. Tests pass: FileWorldDefinitionTest (load-from-file world contents + fresh-each-call + broken-file SceneScriptException), SceneResolverTest (file->FileWorldDefinition, known id->existing def, unknown non-file->fail-fast), CommandLineTest (isAcceptableWorldArg). AC#1-5 met. Next: full just test + detekt + manual CLI render.

VERIFIED. just test (= gradlew clean check, incl detekt) GREEN; gradlew build assembles. Manual CLI (coverage-excluded Main glue): (a) gradlew run --world=scenes/Sample.scene.kts --tracer=WHITTED --renderer=SEQUENTIAL --resolution=720p -> rendered a valid 1280x720 PNG (yellow matte sphere, red phong sphere w/ specular highlight, ground/ceiling planes, shadows) matching the YellowAndRedSphere built-in it mirrors (AC#1); (b) --world=DoesNotExist.kt -> fails fast via Clikt with 'Unknown world ... Pass a built-in scene id or the path of an existing *.scene.kts file' + scene list, no render (TASK-15 fail-fast preserved); (c) --world=YellowAndRedSphere.kt -> built-in still renders unchanged (AC#3). Fixed a Main-glue bug: outputPngFileName on a path value leaked dir separators into the PNG path; Main now bases the output name on File(world).name (built-in ids unchanged). Fat-jar/dist impact: scripting-jvm-host pulls kotlin-compiler-embeddable onto the runtime classpath, +~56MB to the distribution. First external-scene compile latency ~1.3s (script compile) on top of ~0.34s ray-tracing; built-in scenes unaffected (script host never invoked). README documents the feature + tradeoffs. New tests (11, all pass): FileWorldDefinitionTest(4), SceneResolverTest(4), CommandLineTest(3). Pre-existing warnings (PlyReader.kt, GridStructuresTest.kt unchecked casts) are unrelated.
<!-- SECTION:NOTES:END -->
