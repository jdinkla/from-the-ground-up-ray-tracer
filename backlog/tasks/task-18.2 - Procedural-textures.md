---
id: TASK-18.2
title: Procedural textures
status: In Progress
assignee:
  - '@claude'
created_date: '2026-06-22 09:41'
updated_date: '2026-06-22 14:51'
labels:
  - enhancement
  - book-parity
dependencies:
  - TASK-18.1
parent_task_id: TASK-18
priority: medium
ordinal: 20000
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Procedural (computed, non-image) textures from the book, built on the Texture abstraction from the infra subtask. Implement Checker3D (solid checker), the planar/spherical/cylindrical 2D checker variants, a Ramp/ColorRamp texture, and a Wireframe texture. These compute color directly from the hit point with no image input.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Checker3D and at least one 2D checker (plane or sphere) render via an SV material
- [x] #2 A ramp texture and a wireframe texture are implemented
- [x] #3 Procedural textures are declarable from the Builder DSL
- [x] #4 Unit tests cover the color-selection logic of the checker and ramp textures
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Study 18.1 Texture infra (Texture, Checker3D, SvMatte, MaterialsScope, IShade u/v/localHitPoint). Confirm objects never set u/v -> drive procedural textures from localHitPoint and compute uv internally where needed.
2. Add PlaneChecker (2D xz-plane checker, optional grout/line colour+width) in textures/.
3. Add SphereChecker (lat/long uv from normalized hit point, horizontal/vertical bands, optional line colour) in textures/.
4. Add Ramp (ColorRamp): pure scalar->colour linear interpolation between two colours; scalar derived from localHitPoint coordinate via a small enum-selected accessor. Keep colour-selection pure+testable.
5. Add Wireframe: edge-vs-interior decision by proximity to cell boundary in xz-plane; configurable wire width; interior vs wire colour. Make decision testable.
6. commonTest unit tests: PlaneChecker (cell A/B straddling boundary + grout), SphereChecker (band selection), Ramp (endpoints + midpoint via shouldBeApprox), Wireframe (edge vs interior). Use testShade fixture + Color shouldBeApprox.
7. AC#3: confirm svMatte/svPhong already accept any Texture -> procedural textures declarable as-is from DSL; no DSL change needed (note it).
8. Example scenes under examples/textures: PlaneChecker + SphereChecker via svMatte (AC#1), plus Ramp/Wireframe demo. Origin-centred per 18.1 mapping limitation.
9. just test green incl detekt; render scenes via ./gradlew run and report observations.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
Implemented four procedural (non-noise) textures on the 18.1 Texture abstraction, plus tests and example scenes. Checker3D from 18.1 reused as-is (not reimplemented).

Textures added (commonMain/.../textures/):
- PlaneChecker.kt: 2D xz-plane checker keyed on floor(x/size)+floor(z/size) parity, with optional grout line (lineWidth/lineColor). Pure colorAt(x,z) for testing.
- SphereChecker.kt: 2D lat/long sphere checker. getColor computes (u,v) from the normalized local hit point (same convention as SphericalMap, since objects never populate sr.u/sr.v) and delegates to pure colorAt(u,v) -> numHorizontal/numVertical bands + optional grout line.
- Ramp.kt (ColorRamp): pure colorAt(scalar) = linear interp color1->color2, scalar clamped to [0,1]. getColor derives the scalar from a configurable axis of the local hit point * frequency, wrapped into [0,1) (periodic banding). Value-driven, NOT noise (noise is 18.3).
- Wireframe.kt: xz-plane grid; points within wireWidth of a cell boundary get wireColor, interior gets fillColor. Pure colorAt(x,z) for testing.

Design choices / 18.1 limitation: objects do not set sr.u/sr.v and Shade.localHitPoint == world hitPoint, so all four textures drive off localHitPoint and (for the sphere checker) compute uv internally. Example objects are origin-centred/axis-aligned so the mapping is correct; noted in each texture's KDoc.

AC#3 (DSL): svMatte/svPhong/svEmissive in MaterialsScope already accept any Texture, so the new procedural textures are declarable from the Builder DSL as-is (e.g. svMatte(id=..., texture=PlaneChecker(...))). No DSL change was needed or made; verified by the example scenes which declare all four via svMatte.

Tests (commonTest/.../textures/, Kotest StringSpec):
- PlaneCheckerTest, SphereCheckerTest: cell-selection across boundaries (colour A vs B straddling a cell edge), grout-line colour, and the getColor path (xz / lat-long). Exact colours via shouldBe.
- RampTest: endpoints + midpoint + arbitrary-colour 0.25 interpolation + out-of-range clamp + axis/frequency getColor path, via Color shouldBeApprox (Fixture).
- WireframeTest: interior vs near-edge decision (x and z), band-width honoured, getColor path.
AC#4 (checker + ramp colour-selection) covered, plus sphere checker and wireframe.

Example scenes (examples/.../textures/, coverage-excluded -> manually rendered):
- ProceduralCheckers.kt (AC#1): PlaneChecker floor + Checker3D sphere via svMatte.
- SphereCheckerScene.kt (AC#1): SphereChecker globe + PlaneChecker floor via svMatte.
- RampAndWireframe.kt (AC#2 demo): Ramp sphere + Wireframe sphere via svMatte.

Manual verification (./gradlew run --tracer=WHITTED --renderer=SEQUENTIAL --resolution=720p, viewed PNGs):
- ProceduralCheckers: floor = light/dark tiles with red grout lines; sphere = gold/blue solid-checker cubes intersecting the surface. Correct.
- SphereCheckerScene: globe = blue/white lat/long bands with black grout converging at the poles; grey plane checker floor. Correct.
- RampAndWireframe: left sphere = blue->orange vertical gradient (periodic); right sphere = black wire grid over light fill. Correct.

just test (= ./gradlew clean check) BUILD SUCCESSFUL, detekt clean (no new baseline entries). Pre-existing unrelated compiler 'Unchecked cast' warnings remain in PlyReader.kt and GridStructuresTest.kt. No existing files modified.
<!-- SECTION:NOTES:END -->
