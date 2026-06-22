---
id: TASK-18
title: Texture and mapping support (book parity)
status: Done
assignee: []
created_date: '2026-06-22 09:41'
updated_date: '2026-06-22 17:03'
labels:
  - enhancement
  - book-parity
dependencies: []
priority: medium
ordinal: 18000
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Umbrella for the book's texture system, which is entirely absent here (textures/, noise/, mappings/ packages are empty). Suffern devotes several chapters to texturing: a Texture abstraction sampled during shading to produce a color, spatially varying materials (SV_Matte/SV_Phong) that read a Texture instead of a constant diffuse color, image textures with Mapping classes that project a 2D image onto 3D objects (sphere, rectangle, light-probe/panoramic), procedural textures, and noise-based textures. This is goal (b) in CLAUDE.md, called out explicitly. Delivered via the subtasks; this parent tracks the overall feature and its example scenes.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Objects can be assigned a texture-driven material via the existing Builder DSL
- [ ] #2 All subtasks are completed and at least one example scene per texture family renders correctly
<!-- AC:END -->

## Final Summary

<!-- SECTION:FINAL_SUMMARY:BEGIN -->
Umbrella complete — all three subtasks delivered the book's texture system (the textures/, mappings/, noise/ packages were empty at start). TASK-18.1 (commit 4d9eb1e): Texture interface + ConstantColor/Checker3D/ImageTexture, Mapping (SphericalMap/RectangularMap/LightProbe), SV materials (SvMatte/SvPhong/SvEmissive on SvLambertian), additive ShadeRec (u/v/localHitPoint), DSL hooks, jvmMain image decode, light-probe environment maps. TASK-18.2 (commit 8d7ad38): procedural PlaneChecker/SphereChecker/Ramp/Wireframe. TASK-18.3 (commit b2115f0): lattice noise (LatticeNoise + LinearNoise/CubicNoise, deterministic SeededRandom) + noise textures (FBm/Turbulence/WrappedFBm/RampFBm-marble/Wood). AC#1 met — objects get a texture-driven material via the existing Builder DSL (svMatte/svPhong/svEmissive accept any Texture, no Builder/WorldScope change needed). AC#2 met — all subtasks Done and at least one example scene per texture family renders correctly (image: TexturedSphere/TexturedRectangle/EnvironmentMapSphere; procedural: ProceduralCheckers/SphereCheckerScene/RampAndWireframe; noise: MarbleScene/NoiseTexturesScene) — all rendered and verified. This closes book-coverage goal (b) from CLAUDE.md (textures). Each subtask was implemented cover-first (commonMain texture/mapping/noise logic unit-tested; image loading + scenes manually verified), reviewed, and committed individually.
<!-- SECTION:FINAL_SUMMARY:END -->
