---
id: TASK-63
title: Reduce fat interfaces that throw UnsupportedOperationException (ISP/Liskov)
status: Done
assignee:
  - '@claude'
created_date: '2026-06-24 22:37'
updated_date: '2026-06-26 21:36'
labels:
  - tech-debt
  - refactoring
  - design
dependencies: []
references:
  - docs/arc42/11_risks_and_technical_debt.md
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
- [x] #1 Interfaces are segregated incrementally, one family at a time (e.g. a sampling-BRDF role split from an evaluation-BRDF role; AreaLighting-only Light capabilities separated)
- [x] #2 Each increment removes UnsupportedOperationException stubs from at least one family by giving implementations only the interface methods they actually support
- [x] #3 Existing pinning tests are preserved, or consciously updated when an interface genuinely changes shape
- [x] #4 No behavior change to rendering output
- [x] #5 ./gradlew clean check is green after each increment
<!-- AC:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
Completed all four families incrementally, each its own green commit:
1) BRDF: split into BRDF{f} + SamplingBRDF{sampleF,Sample} + ReflectanceBRDF{rho}; each BRDF implements only supported roles (removed 6 stubs).
2) BTDF: narrowed the interface to {sampleF,isTir} (dropped f/rho, which every impl stubbed and no caller used).
3) Tracer/MultipleObjects: removed the gratuitous throwing 3-arg trace override so it inherits Tracer's default; updated the pinning test to assert delegation.
4) AreaLight: split DirectLight:Light{l,getDirection,inShadow} for the scalar lights; AreaLight is a Light (not DirectLight) and no longer an ILightSource (it holds one). Removed 5 throwing stubs; direct-lighting shade loops now filterIsInstance<DirectLight>.
Pinning tests for the removed stubs were deleted/updated consciously (AC#3). No remaining UnsupportedOperationException in brdf/btdf/lights/tracers. clean check green after each increment; rendered AREA (AreaShadedSpheres) and Whitted (YellowAndRedSphere) scenes to confirm unchanged rendering (AC#4).
<!-- SECTION:NOTES:END -->
