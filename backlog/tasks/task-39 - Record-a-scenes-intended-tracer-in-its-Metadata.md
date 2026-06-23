---
id: TASK-39
title: Record a scene's intended tracer in its Metadata
status: In Progress
assignee:
  - '@claude'
created_date: '2026-06-23 19:06'
updated_date: '2026-06-23 20:32'
labels:
  - examples
  - tooling
dependencies: []
ordinal: 42000
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Several example scenes only render correctly with a specific tracer, but that intent is not machine-readable — it lives in free-text descriptions (e.g. MultipleObjects.kt says 'Use MultipleObjects tracer', the lights/area scenes need AREA, CornellBox needs path tracing). The tracer is currently a pure Context/CLI choice (no scene declares one), so any tool or default that renders a scene must guess. This surfaced in the TASK-38 scene audit: its SUSPECT (near-black) list flags MultipleObjects.kt, CornellBox.kt, etc. not because they are broken but because they were rendered with the wrong tracer, forcing a 'may just need a different tracer' caveat. Add an optional preferred-tracer hint to the scene Metadata so scenes can declare the tracer they are designed for, defaulting to today's behavior when absent. Then the audit (and potentially the Swing UI / CLI default) can render each scene with its intended tracer, sharpening black-image detection and reducing false positives. Out of scope: forcing a tracer (the --tracer CLI flag must still override); reworking the tracer selection pipeline beyond reading the hint.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Scene Metadata can carry an optional preferred tracer (referencing the existing Tracers enum), set via the MetadataScope DSL; scenes that set nothing behave exactly as today (no behavior change for existing scenes).
- [x] #2 At least the clearly tracer-coupled scenes declare their intended tracer (e.g. MultipleObjects.kt -> MULTIPLE_OBJECTS, the lights/area scenes -> AREA, CornellBox.kt -> path tracing).
- [x] #3 The TASK-38 audit renders each scene with its declared tracer when present (falling back to its current heuristic otherwise), so those scenes no longer appear as false SUSPECT near-black renders.
- [x] #4 The --tracer CLI flag still overrides any scene hint; the metadata hint is a default, not a lock.
- [x] #5 Testable core (reading/defaulting the hint, audit tracer selection from it) covered by frozen unit tests; DSL/scene wiring in examples verified by rerunning ./gradlew audit and confirming the affected scenes are no longer flagged. Full build incl. detekt stays green.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Add optional preferredTracer: Tracers? = null to Metadata (commonMain); default null = today's behavior. Cover-first: extend MetadataScopeTest + a Metadata read/default test.
2. Add preferredTracer var + preferredTracer(Tracers) setter to MetadataScope; thread into metadata getter. Frozen test pins default-null and explicit-set.
3. Audit: keep chooseTracer(world):AuditTracer (heuristic) and its frozen AuditTracerTest unchanged. Add new auditTracer(world):Tracers that returns world.metadata.preferredTracer when present else maps heuristic AREA->Tracers.AREA / WHITTED->Tracers.WHITTED. New tests pin: hint wins, fallback to heuristic when absent.
4. Wire AuditMain.renderStatus to use auditTracer(world).create instead of chooseTracer mapping. (coverage-excluded glue, verify via audit run.)
5. Declare hints in tracer-coupled scenes: MultipleObjects.kt->MULTIPLE_OBJECTS, CornellBox.kt->PATH_TRACE, area scenes (AreaShadedSpheres, TwoAreaShadedSpheres, TwoSpheresInRectangleLight, FourAreaShadedBoxes)->AREA. (examples/**, verify via audit.)
6. Verify CLI --tracer override path: CommandLine.determineTracer reads only the flag, never metadata, so flag always wins. Confirm by reading + reasoning (no metadata in CLI path).
7. ./gradlew audit before/after for MultipleObjects.kt + CornellBox.kt; ./gradlew build green incl detekt.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
Implemented the optional preferred-tracer hint end-to-end.

CORE (commonMain, cover-first frozen tests):
- Metadata: added optional 'preferredTracer: Tracers? = null' (default null = today's behaviour). New MetadataTest pins null-default and explicit-value reading.
- MetadataScope: added 'preferredTracer' var + 'preferredTracer(Tracers)' setter, threaded into the metadata getter. Extended MetadataScopeTest pins null-default, setter, and direct assignment.

AUDIT (jvmMain, cover-first):
- Kept chooseTracer(world):AuditTracer heuristic and its existing AuditTracerTest UNCHANGED (frozen).
- Added auditTracer(world):Tracers = world.metadata.preferredTracer ?: heuristic(AREA/WHITTED). New AuditTracerTest cases pin: fallback-to-heuristic when absent, and declared hint wins (MULTIPLE_OBJECTS over WHITTED-heuristic; PATH_TRACE over AREA-heuristic).
- AuditMain.renderStatus now uses auditTracer(world).create (was a 2-case AuditTracer->creator when). Removed now-unused AreaLighting/Whitted imports.
- ReportFormatter SUSPECT caveat updated to mention the declared-tracer path (ReportFormatterTest does not assert on the wording; still green).

SCENES (examples/**, JaCoCo-excluded, verified via ./gradlew audit):
- MultipleObjects.kt -> MULTIPLE_OBJECTS
- CornellBox.kt -> PATH_TRACE
- AreaShadedSpheres.kt, TwoAreaShadedSpheres.kt, TwoSpheresInRectangleLight.kt, FourAreaShadedBoxes.kt -> AREA (the scenes whose descriptions say 'Use area tracer').

AC#3 audit before/after (health render 160x90):
- BEFORE suspects: MultipleObjects.kt 100%, Template.kt 100%, CornellBox.kt 100%.
- AFTER suspects: MultipleObjects.kt 100%, Template.kt 100%. CornellBox.kt is FIXED (PATH_TRACE renders it; it was a false positive from the AREA heuristic).
- MultipleObjects.kt is NOT a false positive: it renders 100% black with WHITTED AND with MULTIPLE_OBJECTS (verified by direct 720p CLI renders + visual inspection). Root cause is the scene's own bug, not tracer choice: it calls pointLight(location=p(3,3,1)) with no ls, and the pointLight DSL default is ls=0.0, so its only light has zero intensity. That is a scene-content defect, OUT OF SCOPE for TASK-39 (reading/recording the hint). The audit is correctly reporting a true near-black render. Flagging for the manager as possible follow-up.
- Template.kt is an intentionally empty template (no camera/objects) -> genuinely black, out of scope.

AC#4 (CLI override): the CLI render path (CommandLine.run -> determineTracer() reads only --tracer, default WHITTED -> Context(usedTracer.create,...)) never reads scene metadata, so --tracer always wins; the hint is consumed only by the audit. Verified by rendering MultipleObjects.kt with --tracer=WHITTED and --tracer=MULTIPLE_OBJECTS (the flag drives the tracer regardless of the scene's MULTIPLE_OBJECTS hint).

CHECK: ./gradlew build (compile + all tests + detekt + jacoco) BUILD SUCCESSFUL.
<!-- SECTION:NOTES:END -->
