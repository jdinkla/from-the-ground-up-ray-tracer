---
id: TASK-18.2
title: Procedural textures
status: In Progress
assignee:
  - '@claude'
created_date: '2026-06-22 09:41'
updated_date: '2026-06-22 14:46'
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
- [ ] #1 Checker3D and at least one 2D checker (plane or sphere) render via an SV material
- [ ] #2 A ramp texture and a wireframe texture are implemented
- [ ] #3 Procedural textures are declarable from the Builder DSL
- [ ] #4 Unit tests cover the color-selection logic of the checker and ramp textures
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
