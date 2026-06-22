---
id: TASK-18.1
title: Texture & mapping infrastructure + image textures
status: Done
assignee:
  - '@claude'
created_date: '2026-06-22 09:41'
updated_date: '2026-06-22 14:44'
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
- [x] #1 A Texture interface exists and is sampled during shading to produce a color
- [x] #2 SV_Matte and SV_Phong render with a Texture as diffuse color, declarable from the DSL
- [x] #3 ImageTexture loads an image file and maps it onto a sphere and a rectangle via Mapping classes
- [x] #4 A light-probe / spherical environment map can be applied (via EnvironmentLight or a textured object)
- [x] #5 Unit tests cover Texture sampling and at least one Mapping in commonTest; image-file loading verified manually per the coverage-excluded-zones rule
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

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
Implemented texture & mapping infrastructure + image textures.

ARCHITECTURE (types + locations):
- commonMain textures/: Texture interface (getColor(IShade):Color); ConstantColor, Checker3D (procedural); Image (in-memory raster, row/col, edge-clamping getColor); ImageTexture (sampling math: mapping -> pixel lookup, with parametric u/v fallback). All sampling math is platform-independent.
- jvmMain textures/ImageReader: ImageIO-backed file decode -> commonMain Image. Only I/O lives here; mirrors utilities/Png.kt.
- commonMain mappings/: Mapping interface (getTexelCoordinates(localHitPoint,hres,vres):Texel(row,col)); SphericalMap (normalises hit point so any sphere radius works), RectangularMap (configurable axes/extents), LightProbe (regular + panoramic).
- commonMain brdf/SvLambertian: spatially-varying Lambertian sampling a Texture for cd.
- commonMain materials/: SvMatte (mirrors Matte, SvLambertian diffuse/ambient), SvPhong (extends SvMatte + constant GlossySpecular), SvEmissive (texture as radiance; doubles as environment-map material and EnvironmentLight material).

ShadeRec change (additive, backward-compatible): added NON-ABSTRACT default members to IShade — localHitPoint (=hitPoint), u, v (=0.0). No abstract members added because a frozen test (MatteAreaLightShadeTest) implements IShade anonymously; defaults keep it and all existing impls compiling. Concrete Shade.localHitPoint became an override (value unchanged). Existing render path and materials behave identically.

DSL: MaterialsScope gained svMatte/svPhong/svEmissive (take a Texture, follow existing idiom). LightsScope gained environmentLight(material, sampler, shadows) wiring EnvironmentLight.

DESIGN CHOICES worth review:
- Environment map (AC#4) uses the TEXTURED-OBJECT route (a large SvEmissive sphere) as the featured scene because it renders directly under any tracer and is fully verifiable; the EnvironmentLight DSL hook is also provided (needs AREA tracer + sampler). Documented in EnvironmentMapSphere.kt.
- Mappings work on objects CENTERED AT THE ORIGIN. Threading local coords through Instance affine transforms (so a translated/scaled textured sphere maps correctly) is NOT done — Instance.hit does not set a local hit point. Out of scope for 18.1; example scenes use origin-centred unit/large spheres and an origin rectangle. Worth a follow-up task if transformed textured objects are needed.

TESTED (commonTest, Kotest StringSpec, shouldBeApprox for colors):
- ConstantColorTest, Checker3DTest (sampling), ImageTextureTest (mapping path + u/v fallback + edge clamp).
- SphericalMapTest, RectangularMapTest, LightProbeTest (known directions -> hand-derived texels).
- SvMatteShadeTest (SvMatte+ConstantColor == Matte; Checker3D varies by hit point), SvPhongShadeTest (SvPhong+ConstantColor == Phong).
- MaterialsScopeTest extended for svMatte/svPhong/svEmissive.

MANUALLY VERIFIED (coverage-excluded: examples + jvm ImageReader I/O) by rendering at 720p WHITTED/SEQUENTIAL:
- TexturedSphere.kt: 4-quadrant grid texture wraps the sphere via SphericalMap, correct shading + floor shadow.
- TexturedRectangle.kt: texture maps onto an xz rectangle via RectangularMap, grid lines visible.
- EnvironmentMapSphere.kt: enclosing SvEmissive sphere fills the background as a spherical env map with a Phong ball in front.
All three render correctly; ImageReader.fromFile('resources/texture-test.png') decodes fine. Added a small bundled 256x128 test image resources/texture-test.png (generated via ImageMagick).
<!-- SECTION:NOTES:END -->

## Final Summary

<!-- SECTION:FINAL_SUMMARY:BEGIN -->
Built the book's foundational texture/mapping layer (textures/, mappings/ packages were empty). Texture interface getColor(IShade): Color with ConstantColor, Checker3D, ImageTexture (sampling math in commonMain over an in-memory Image; file decode in jvmMain ImageReader via ImageIO). Mapping interface with SphericalMap, RectangularMap, LightProbe (regular+panoramic). Spatially-varying materials SvMatte/SvPhong/SvEmissive built on a new SvLambertian BRDF — shading is byte-identical to Matte/Phong with the constant cd replaced by texture.getColor(sr) (reviewer confirmed SvMatte+ConstantColor == Matte). DSL: svMatte/svPhong/svEmissive on MaterialsScope (string-id idiom), environmentLight on LightsScope. The hit record (IShade/Shade) was extended STRICTLY ADDITIVELY (localHitPoint=hitPoint, u=0.0, v=0.0 defaults; one override keyword) — reviewer verified no existing object hit()/tracer/material changed, existing shading inert and unchanged, full pre-existing suite green. AC1-5 all met. Cover-first: commonTest covers ConstantColor/Checker3D/ImageTexture sampling + all three mappings with independently hand-derived expected values (not round-trips) + SvMatte/SvPhong shade tests + DSL test. Image-file loading and the 3 example scenes (TexturedSphere via SphericalMap, TexturedRectangle via RectangularMap, EnvironmentMapSphere via a large SvEmissive sphere) verified by rendering — reviewer independently re-rendered all three at 720p and confirmed valid non-trivial PNGs. Added resources/texture-test.png (493 bytes, 256x128). detekt clean, no baseline entries added. Verified via just test (398 tests + detekt) BUILD SUCCESSFUL. Committed as 4d9eb1e. Documented design choices / follow-ups: (1) env-map featured route is a textured SvEmissive sphere (renders under any tracer); EnvironmentLight DSL hook also wired (needs AREA tracer). (2) Mappings assume origin-centered objects — Instance affine local coords not threaded (Instance.hit sets no local hit point); reasonable follow-up if transformed textured objects are needed. (3) NIT from review: LightProbe regular mode uses acos(z) (forward=+z); exact book parity with a real mirror-ball photo would use acos(-z) — internally consistent, no AC broken, minor follow-up.
<!-- SECTION:FINAL_SUMMARY:END -->
