---
id: TASK-54
title: >-
  Fix AreaLight.l reading the receiving surface's getLe instead of the light's
  own emitted radiance (AREA lights ~20x too dim)
status: Done
assignee:
  - '@claude'
created_date: '2026-06-24 11:21'
updated_date: '2026-06-24 13:10'
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
- [x] #1 AreaLight.l returns the AREA light emitter's own emitted radiance (its material getLe), not the receiving surface's getLe
- [x] #2 The previously-frozen MatteAreaLightShadeTest is updated to the corrected expected radiance (this is a documented behavior change, not a refactor) and any other affected area-lighting tests are updated consistently
- [x] #3 AREA example scenes (e.g. AreaShadedSpheres) are re-rendered and confirmed correctly exposed (not blown out / not over-dim); detekt and the full build stay green
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Read AreaLight.l, AreaLight emitter (getLightMaterial), Matte.globalDirect/areaLightShade, Emissive.getLe, affected tests. 2. Fix AreaLight.l to return getLightMaterial().getLe(sr) (emitter's own le) instead of sr.material.getLe (receiver) — mirror Matte.globalDirect. 3. Update MatteAreaLightShadeTest: it left AreaLight.material=null (would now throw); assign an Emissive emitter and recompute expected le=ce*ls independently (with comment on corrected physics). Keep AreaLightTest's l-test intent correct. 4. Re-render AreaShadedSpheres + run ./gradlew audit; confirm no AREA scene near-black/blown-out. Clean up my PNGs. 5. ./gradlew clean check green.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
Fixed AreaLight.l to return getLightMaterial().getLe(sr) (emitter's own le) instead of sr.material.getLe (receiver). Removed the now-unused 'world' param from the 3-arg l(sr, sample) — detekt UnusedParameter is active — and updated its 4 callers (Matte, Phong, SvMatte, SvPhong). Updated characterization tests that constructed AreaLight without a material (would now throw on getLightMaterial): MatteAreaLightShadeTest, AreaLightTest, PhongTest, SvMatteTest, SvPhongTest. Derived expected values independently: emitter Le = ce*ls. MatteGlobalShadeTest already used the correct emitter pattern (globalDirect) — unchanged.

Verification: ./gradlew clean check is green (only pre-existing Unchecked-cast warnings in PlyReader/GridStructuresTest, unrelated). ./gradlew audit: 'No scene rendered (near-)black above the threshold' for all 8 AREA scenes (only pre-existing World61.kt fails on missing Bunny4K.ply download). Rendered AreaShadedSpheres at 720p with AREA tracer: per-channel mean ~0.33-0.35 (was ~0.22-0.23 pre-fix), 1.1% blown-out, well-exposed — spheres visibly brighter/more vividly lit, not washed out. Render kept in session scratchpad; no PNG left in the parent workspace (pre-existing dated PNGs untouched).
<!-- SECTION:NOTES:END -->

## Final Summary

<!-- SECTION:FINAL_SUMMARY:BEGIN -->
Fixed AreaLight.l to return the AREA light emitter's OWN emitted radiance (getLightMaterial().getLe(sr)) instead of the receiving surface's sr.material.getLe(sr), matching Matte.globalDirect (commit 6f043fd) and Suffern ch.18 AreaLight::L. Previously AREA lights delivered only the receiver's cd*kd / cs*ks fraction of the panel's ce*ls intensity (~20x too dim). The now-unused world param was dropped from l(sr, sample) (the 3-arg l lives only on AreaLight, not the Light interface, so no other subclass needed syncing); the four call sites (Matte, Phong, SvMatte, SvPhong) were updated. Only the radiance SOURCE changed; the nDotWi / G/pdf geometry terms are unchanged. As a deliberate behavior change (not a refactor), the previously-frozen MatteAreaLightShadeTest and the affected AreaLightTest/PhongTest/SvMatteTest/SvPhongTest were updated to independently-derived corrected values (Le = emitter ce*ls, chosen distinct from the receiver's cd*kd so a regression would fail), each with an explanatory comment. Verified: ./gradlew clean check + detekt green; reviewer PASS (confirmed fix correctness, full signature-change propagation with no remaining 3-arg callers and no NPE on production areaLight DSL paths, independent/discriminating test re-derivations, detekt baseline untouched); ./gradlew audit reports no near-black AREA scene; AreaShadedSpheres re-rendered brighter and correctly exposed (per-channel mean ~0.34 vs ~0.22 pre-fix). Committed cb89fca.
<!-- SECTION:FINAL_SUMMARY:END -->
