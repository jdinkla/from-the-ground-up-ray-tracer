---
id: TASK-64
title: Remove the dead deprecated RayCast tracer
status: Done
assignee:
  - '@claude'
created_date: '2026-06-24 22:37'
updated_date: '2026-06-26 20:44'
labels:
  - tech-debt
dependencies: []
references:
  - TECH_DEBT_REPORT.md
priority: low
ordinal: 67000
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
tracers/RayCast.kt only throws 'RayCast tracer is deprecated and must not be used' from its init. It is not in the Tracers enum (which lists WHITTED/AREA/MULTIPLE_OBJECTS/PATH_TRACE/GLOBAL_TRACE) and exists solely alongside a test that pins its refusal to construct. It is dead code that exists only to forbid itself: confusing to readers, and a test that guards a non-feature. Minimal but pure noise. See TECH_DEBT_REPORT.md section P3 #8.

Locations: src/commonMain/.../tracers/RayCast.kt:12; tracers/Tracers.kt (no RAY_CAST entry); RayCastTest.kt.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 RayCast.kt and its pinning test RayCastTest.kt are removed, OR a one-line comment is added to RayCast.kt explaining why it is kept as a deliberate tombstone
- [x] #2 The Tracers enum and any references remain consistent (no dangling references)
- [x] #3 ./gradlew clean check is green
<!-- AC:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
Removed RayCast.kt and RayCastTest.kt (dead code: not in Tracers enum, only referenced by its self-forbidding test). Grep confirmed no other references. ./gradlew clean check green.
<!-- SECTION:NOTES:END -->
