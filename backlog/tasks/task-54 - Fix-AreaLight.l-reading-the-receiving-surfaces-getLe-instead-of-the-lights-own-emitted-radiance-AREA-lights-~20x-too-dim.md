---
id: TASK-54
title: >-
  Fix AreaLight.l reading the receiving surface's getLe instead of the light's
  own emitted radiance (AREA lights ~20x too dim)
status: In Progress
assignee:
  - '@claude'
created_date: '2026-06-24 11:21'
updated_date: '2026-06-24 12:53'
labels:
  - bug
  - lights
  - arealights
dependencies: []
priority: medium
ordinal: 57000
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Discovered during TASK-48 (hybrid GlobalTrace tracer). AreaLight.l computes the incoming radiance from the RECEIVING surface's material (sr.material.getLe) rather than the AREA light's OWN emitter material. As a result AREA lights are roughly 20x too dim under the AREA tracer / areaLightShade path. TASK-48 deliberately did NOT touch this (it would change every AREA scene and break the frozen MatteAreaLightShadeTest), and instead gave GlobalTrace its own correct direct term (Matte.globalDirect, which uses light.getLightMaterial().getLe). This task fixes the shared AreaLight.l for the AREA tracer. NOTE: the existing MatteAreaLightShadeTest is a CHARACTERIZATION test pinning the current (buggy) behavior, so this is a BEHAVIOR CHANGE, not a pure refactor: the test must be updated to the corrected expectation as part of the change (and any AREA example scenes re-checked for brightness/exposure). Cross-check against Suffern ch.18 (area lighting) AreaLight::L and the emitter's getLe. See materials/Matte.globalDirect (commit 6f043fd) for the correct emitter-radiance read.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 AreaLight.l returns the AREA light emitter's own emitted radiance (its material getLe), not the receiving surface's getLe
- [ ] #2 The previously-frozen MatteAreaLightShadeTest is updated to the corrected expected radiance (this is a documented behavior change, not a refactor) and any other affected area-lighting tests are updated consistently
- [ ] #3 AREA example scenes (e.g. AreaShadedSpheres) are re-rendered and confirmed correctly exposed (not blown out / not over-dim); detekt and the full build stay green
<!-- AC:END -->
