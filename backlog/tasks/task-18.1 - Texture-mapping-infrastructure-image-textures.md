---
id: TASK-18.1
title: Texture & mapping infrastructure + image textures
status: In Progress
assignee:
  - '@claude'
created_date: '2026-06-22 09:41'
updated_date: '2026-06-22 14:25'
labels:
  - enhancement
  - book-parity
dependencies: []
parent_task_id: TASK-18
priority: medium
ordinal: 19000
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Foundational texture layer plus image textures. Add a Texture abstraction (getColor(shadeRec): Color), the spatially varying materials SV_Matte and SV_Phong that sample a Texture for their diffuse color, and a Mapping abstraction that converts a hit point / local coords into texture (u,v) or image (row,col). Implement ImageTexture backed by a loaded raster image plus the book's common mappings: SphericalMap, RectangularMap, and LightProbe (regular + panoramic) for environment maps. Thread texture coordinates through the hit record (ShadeRec) as needed. Image loading is JVM I/O (jvmMain); the Texture/Mapping/SV-material types are core (commonMain).
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 A Texture interface exists and is sampled during shading to produce a color
- [ ] #2 SV_Matte and SV_Phong render with a Texture as diffuse color, declarable from the DSL
- [ ] #3 ImageTexture loads an image file and maps it onto a sphere and a rectangle via Mapping classes
- [ ] #4 A light-probe / spherical environment map can be applied (via EnvironmentLight or a textured object)
- [ ] #5 Unit tests cover Texture sampling and at least one Mapping in commonTest; image-file loading verified manually per the coverage-excluded-zones rule
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. ShadeRec (additive, backward-compat): add non-abstract default members localHitPoint (=hitPoint), u, v (=0.0) to IShade so existing anonymous IShade impls keep compiling; Shade already exposes localHitPoint. No abstract members added (a frozen test implements IShade anonymously).
2. Texture (commonMain): interface Texture { fun getColor(sr: IShade): Color }. Implement ConstantColor and a Checker3D for unit-testable sampling.
3. Mapping (commonMain): interface Mapping { fun getTexelCoordinates(localHitPoint, hres, vres): Pair<row,col> }. Implement SphericalMap, RectangularMap, LightProbe (regular + panoramic). Pure math, unit-tested.
4. Image (commonMain): abstract in-memory raster Image { width,height, getColor(x,y) } + ImageTexture(image, mapping?) whose sampling math (mapping->pixel lookup) lives in commonMain. Spherical fallback when mapping==null (uses sr normal direction / spherical coords) for environment maps.
5. JVM I/O (jvmMain): ImageIO-backed loader producing an Image (mirror Png/File). Keep decode in jvmMain only.
6. SV materials (commonMain): SV_Lambertian + SV_GlossySpecular BRDFs that sample a Texture; SV_Matte (mirrors Matte) and SV_Phong (mirrors Phong) reading a Texture for diffuse color.
7. DSL (commonMain): MaterialsScope.svMatte(id, texture, ka, kd) and svPhong(...) following existing material-declaration idiom.
8. EnvironmentLight (AC#4): wire an ImageTexture-backed Emissive-like material as the environment map; document choice (textured EnvironmentLight material vs large textured sphere).
9. Tests (commonTest): ConstantColor/Checker3D sampling; SphericalMap + RectangularMap known directions->(u,v); SV_Matte/SV_Phong shaded color with a stub texture; ImageTexture sampling over a fake in-memory raster + LightProbe.
10. Example scenes (src/examples): sphere+image texture, rectangle+image texture, environment map. Add a tiny bundled test image to resources if needed. Manually verify renders.
11. just test green; detekt clean.
<!-- SECTION:PLAN:END -->
