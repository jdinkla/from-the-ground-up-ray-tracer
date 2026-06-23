---
id: TASK-39
title: Record a scene's intended tracer in its Metadata
status: In Progress
assignee:
  - '@claude'
created_date: '2026-06-23 19:06'
updated_date: '2026-06-23 20:22'
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
- [ ] #1 Scene Metadata can carry an optional preferred tracer (referencing the existing Tracers enum), set via the MetadataScope DSL; scenes that set nothing behave exactly as today (no behavior change for existing scenes).
- [ ] #2 At least the clearly tracer-coupled scenes declare their intended tracer (e.g. MultipleObjects.kt -> MULTIPLE_OBJECTS, the lights/area scenes -> AREA, CornellBox.kt -> path tracing).
- [ ] #3 The TASK-38 audit renders each scene with its declared tracer when present (falling back to its current heuristic otherwise), so those scenes no longer appear as false SUSPECT near-black renders.
- [ ] #4 The --tracer CLI flag still overrides any scene hint; the metadata hint is a default, not a lock.
- [ ] #5 Testable core (reading/defaulting the hint, audit tracer selection from it) covered by frozen unit tests; DSL/scene wiring in examples verified by rerunning ./gradlew audit and confirming the affected scenes are no longer flagged. Full build incl. detekt stays green.
<!-- AC:END -->
