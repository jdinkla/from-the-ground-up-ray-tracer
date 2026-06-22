---
id: TASK-18.1
title: Texture & mapping infrastructure + image textures
status: In Progress
assignee:
  - '@claude'
created_date: '2026-06-22 09:41'
updated_date: '2026-06-22 14:22'
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
