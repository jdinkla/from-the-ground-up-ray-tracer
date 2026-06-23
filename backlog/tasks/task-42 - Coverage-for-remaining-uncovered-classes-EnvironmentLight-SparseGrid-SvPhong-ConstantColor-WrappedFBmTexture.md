---
id: TASK-42
title: >-
  Coverage for remaining uncovered classes: EnvironmentLight, SparseGrid,
  SvPhong, ConstantColor, WrappedFBmTexture
status: Done
assignee:
  - '@claude'
created_date: '2026-06-23 19:15'
updated_date: '2026-06-23 20:02'
labels:
  - examples
  - coverage
dependencies: []
priority: low
ordinal: 45000
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
The TASK-38 audit lists a heterogeneous remainder of implemented-but-unexercised classes with no example scene: EnvironmentLight (light; environmentLight() already exists in LightsScope DSL), SparseGrid (acceleration structure), SvPhong (spatially-varying Phong material), and the textures ConstantColor and WrappedFBmTexture. Add example scene(s) covering them. Some may already be DSL-reachable (EnvironmentLight is); others (SparseGrid as an objects{} wrapper like grid/kdtree, the SvPhong material, the two textures) may need a DSL/materials check or a small builder. Confirm reachability per class and add the smallest wiring needed, then a scene. Scenes are coverage-excluded (verify by render); any commonMain wiring added is cover-first (frozen test).
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Example scene(s) instantiate EnvironmentLight, SparseGrid, SvPhong, ConstantColor, and WrappedFBmTexture
- [x] #2 Re-running ./gradlew audit shows all five classes removed from their respective 'Uncovered classes' sections
- [x] #3 Any new DSL/materials wiring added in commonMain to reach these classes is covered by a frozen unit test written before the scene
- [x] #4 New scenes render to non-near-black images (verified manually) and are not flagged Suspect/Failed by the audit
- [x] #5 Full build stays green: ./gradlew build (compile + test + detekt) passes
<!-- AC:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
Three scenes added under src/examples:
- TextureMaterialsScene.kt: svPhong material backed by a WrappedFBmTexture (marble) + svMatte backed by ConstantColor -> covers SvPhong, WrappedFBmTexture, ConstantColor. No DSL change needed (svPhong already existed; textures constructed directly and read by TextureCollector via the materials).
- SparseGridScene.kt: a 6x6 sphere field in a new sparseGrid{} block -> covers SparseGrid.
- EnvironmentLightScene.kt: environmentLight + ambient-lit concaveSphere sky dome -> covers EnvironmentLight.
DSL (commonMain, cover-first): added ObjectsScope.sparseGrid{} mirroring grid{}/kdtree{}; frozen test 'should handle sparseGrid' added to ObjectsScopeTest BEFORE the scene.

EnvironmentLight findings (took iteration):
1. Sampler: MultiJittered/Jittered/Regular underfill the hemisphere buffer (TASK-31 bug) -> IndexOutOfBounds; used PureRandom.
2. Tracer: EnvironmentLight is summed by Matte.shade (WHITTED) but NOT by Matte.areaLightShade (AREA filters to filterIsInstance<AreaLight>()), so the scene needs WHITTED, not AREA. Avoided registering any Emissive material (which would push the audit's chooseTracer onto AREA); the dome is a plain ambient-lit matte and the env light's radiance Emissive is passed unregistered. Declared description('Use whitted tracer ...') for the eventual TASK-39 hint.
3. Geometry: dome must be concaveSphere (inward normals -> visible from inside) and the env light shadows=false (the dome would otherwise occlude every hemisphere ray). Boosted env radiance to 4.0 since the /pi BRDF makes single-bounce hemisphere light dim.
OBSERVATION (not fixed here): the AREA path ignoring EnvironmentLight looks like an incomplete port — worth a possible follow-up.
Verified: ./gradlew audit -> Accel 3/3, Materials 10/10, Textures 12/12, Lights 6/6; no new Suspect/Failed; all three eyeballed at 720p; ./gradlew build (detekt + tests) green.
<!-- SECTION:NOTES:END -->

## Final Summary

<!-- SECTION:FINAL_SUMMARY:BEGIN -->
Added TextureMaterialsScene, SparseGridScene and EnvironmentLightScene covering the last 5 implemented-but-unexercised classes: SvPhong, ConstantColor, WrappedFBmTexture, SparseGrid, EnvironmentLight. Added a sparseGrid{} DSL method (cover-first frozen test). EnvironmentLight needed real digging: PureRandom sampler (TASK-31 bug), WHITTED tracer (AREA's Matte.areaLightShade ignores non-AreaLight lights), concaveSphere dome + shadows=false. Audit now maxes every category except the MeshTriangle base class; ./gradlew build green; all scenes eyeballed.
<!-- SECTION:FINAL_SUMMARY:END -->
