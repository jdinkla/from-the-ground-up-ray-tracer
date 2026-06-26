---
id: TASK-63
title: Reduce fat interfaces that throw UnsupportedOperationException (ISP/Liskov)
status: In Progress
assignee:
  - '@claude'
created_date: '2026-06-24 22:37'
updated_date: '2026-06-26 21:16'
labels:
  - tech-debt
  - refactoring
  - design
dependencies: []
references:
  - TECH_DEBT_REPORT.md
priority: low
ordinal: 66000
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Several reflectance/transmittance/light/tracer interfaces declare methods that not all implementations support; the unsupported ones throw UnsupportedOperationException. BRDFs: PerfectSpecular, GlossySpecular, FresnelReflector, SvLambertian (f / rho / sampleF). BTDFs: PerfectTransmitter, FresnelTransmitter. AreaLight: sample / getNormal / getDirection (when no AreaLighting tracer). MultipleObjects.trace. The behavior is pinned by tests, so it is intentional, but it is an interface-segregation / Liskov-substitution violation: callers must know which concrete type they hold to avoid runtime throws, and the compiler cannot help.

Mostly inherited from the book's class hierarchy, so low urgency, but a real design constraint on evolving the shading code. This is a larger, incremental effort (L), NOT a rewrite: narrow the interfaces one family at a time, keeping the pinning tests. See TECH_DEBT_REPORT.md section P3 #7.

Locations: src/commonMain brdf/, btdf/, lights/AreaLight.kt, tracers/MultipleObjects.kt; pinning tests under commonTest/.../brdf/*UnsupportedOperationTest.kt.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Interfaces are segregated incrementally, one family at a time (e.g. a sampling-BRDF role split from an evaluation-BRDF role; AreaLighting-only Light capabilities separated)
- [ ] #2 Each increment removes UnsupportedOperationException stubs from at least one family by giving implementations only the interface methods they actually support
- [ ] #3 Existing pinning tests are preserved, or consciously updated when an interface genuinely changes shape
- [ ] #4 No behavior change to rendering output
- [ ] #5 ./gradlew clean check is green after each increment
<!-- AC:END -->
