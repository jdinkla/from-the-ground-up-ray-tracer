---
id: TASK-46
title: >-
  Specular and glossy reflection in the path tracer (Reflective/GlossyReflector
  pathShade)
status: Done
assignee:
  - '@claude'
created_date: '2026-06-24 08:23'
updated_date: '2026-06-24 08:42'
labels:
  - book-coverage
  - global-illumination
  - materials
  - chapter-26
dependencies: []
references:
  - Chapter 26 Global Illumination _ Ray Tracing from the Ground Up.pdf
priority: medium
ordinal: 49000
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
The PATH_TRACE tracer (Suffern ch. 26) currently produces global illumination only for diffuse and emissive surfaces: only Matte and Emissive override IMaterial.pathShade, while Reflective, GlossyReflector, Transparent and Dielectric fall back to the default that returns BLACK. So a mirror or glossy-reflective surface renders black under PATH_TRACE, and the path tracer cannot produce specular-to-diffuse light transport or the caustics in book Figures 26.8 (flat mirror) and 26.9 (cardioid caustic from a concave cylindrical reflector). The reflectance sampling already exists (PerfectSpecular.sampleF, GlossySpecular.sampleF), so this is a small material-level addition that mirrors Matte.pathShade (recurse via world.tracer.trace(ray, depth+1)). See book Listing 26.5 (Reflective path_shade) and exercise 26.9 (GlossyReflector).
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Reflective.pathShade samples the perfect-specular direction (PerfectSpecular.sampleF), traces the reflected ray one level deeper via world.tracer.trace(ray, depth+1) and returns the weighted incoming radiance, matching book Listing 26.5
- [x] #2 GlossyReflector.pathShade does the equivalent using GlossySpecular.sampleF (book exercise 26.9)
- [x] #3 A reflective object rendered with the PATH_TRACE tracer is no longer black; rendering a GI scene (e.g. CornellBox variant) with a mirror shows reflections
- [x] #4 A new auto-discovered example scene demonstrates a reflective caustic: matte plane + emissive sphere + flat mirror (book Figure 26.8) with preferredTracer(PATH_TRACE); optionally a concave cylindrical reflector for the cardioid caustic (Figure 26.9)
- [x] #5 The two pathShade overrides (commonMain) are covered by frozen unit tests per the cover-first rule and specs/testing.md; detekt and the full build stay green; the example scene is verified manually by rendering
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Cover-first: write frozen tests ReflectivePathShadeTest + GlossyReflectorPathShadeTest in src/commonTest (mirror MattePathShadeTest/EmissivePathShadeTest), using a fake IWorld/Tracer that records the recursion depth and returns a fixed incoming colour; pin the analytic per-sample weight (PerfectSpecular: kr/abs(n.wi) * (n.wi) = kr with pdf=1; GlossySpecular: color*(n.wi)/pdf reduces to cs*ks). Confirm they FAIL first (default pathShade returns BLACK).
2. Implement Reflective.pathShade: sample PerfectSpecular.sampleF(sr,wo), trace reflected ray at depth+1, return sample.color*incoming*(n.wi), guarding tracer==null -> BLACK (book Listing 26.5).
3. Implement GlossyReflector.pathShade: delegate to existing glossyReflection(world,sr) (already exactly the path-shade form per exercise 26.9).
4. Confirm tests pass; verify they are unchanged from the cover-first version.
5. Add auto-discovered example scene ReflectiveCaustic.kt (matte plane + emissive sphere + flat mirror, Fig 26.8) under examples/globalillumination with preferredTracer(PATH_TRACE).
6. Render the scene (just run) to confirm non-black/coherent output (examples are coverage-excluded -> manual verify).
7. Run ./gradlew clean check (just test) and ensure detekt+build green.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
Cover-first: added ReflectivePathShadeTest + GlossyReflectorPathShadeTest. Confirmed the 'weights' assertions FAIL against current code. Correction to the task description: Reflective/GlossyReflector do NOT fall back to the BLACK default — they extend Phong->Matte and currently inherit Matte.pathShade (a diffuse cosine bounce), so a mirror under PATH_TRACE renders as a diffuse grey surface, not black, and specular-to-diffuse transport/caustics are still absent. The fix (override pathShade in both) is the same.

Implementation complete.
Production (commonMain):
- Reflective.pathShade (Listing 26.5): samples PerfectSpecular.sampleF, traces reflected ray at depth+1, returns sample.color*incoming*(n.wi); BLACK when no tracer. Since sampleF gives color=cr*(kr/|n.wi|), pdf=1, the weight collapses to cr*kr*incoming (geometry retained, pdf=1 -- unlike Matte's cosine bounce).
- GlossyReflector.pathShade (exercise 26.9): delegates to the existing glossyReflection() private method, which already computes color*incoming*(n.wi)/pdf == cr*kr*incoming.
Both previously inherited Matte.pathShade (a diffuse bounce), so they overrode it now.

Tests (commonTest, cover-first, frozen): ReflectivePathShadeTest, GlossyReflectorPathShadeTest. The 'weights' assertions were confirmed RED before the override and GREEN after, unchanged. Each pins the analytic weight (direction-independent despite random glossy sampling) plus depth+1 recursion and the null-tracer BLACK fallback.

Example scene (examples/**, coverage-excluded -> manual verify): ReflectiveCaustic.kt (Fig 26.8) = matte floor + emissive sphere + flat vertical mirror, preferredTracer(PATH_TRACE). Rendered at 720p/FORK_JOIN/PATH_TRACE in ~7s: non-black and coherent -- emissive sphere visible, its reflection visible in the mirror (so the specular bounce is followed), and the floor catches the warm caustic glow. Expected Monte-Carlo grain (100 spp, no AA).

Verification: ./gradlew clean check GREEN (detekt + all tests). Two pre-existing unchecked-cast compile warnings (PlyReader.kt, GridStructuresTest.kt) are unrelated to this change.
<!-- SECTION:NOTES:END -->

## Final Summary

<!-- SECTION:FINAL_SUMMARY:BEGIN -->
Added pathShade overrides to Reflective (PerfectSpecular.sampleF, book Listing 26.5) and GlossyReflector (GlossySpecular.sampleF via existing glossyReflection, exercise 26.9), so reflective/glossy surfaces follow specular bounces under the PATH_TRACE tracer instead of inheriting Matte's diffuse bounce. Added frozen cover-first tests (ReflectivePathShadeTest, GlossyReflectorPathShadeTest) that pin the new behavior, and an auto-discovered example scene ReflectiveCaustic.kt (matte floor + emissive sphere + flat mirror, book Fig 26.8, preferredTracer PATH_TRACE). Verified: ./gradlew clean check green (detekt + tests), reviewer PASS, and scene rendered non-black at 720p showing the mirror reflection of the emissive sphere. Committed 22396c5.
<!-- SECTION:FINAL_SUMMARY:END -->
