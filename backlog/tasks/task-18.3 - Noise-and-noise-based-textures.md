---
id: TASK-18.3
title: Noise and noise-based textures
status: In Progress
assignee:
  - '@claude'
created_date: '2026-06-22 09:41'
updated_date: '2026-06-22 16:57'
labels:
  - enhancement
  - book-parity
dependencies:
  - TASK-18.1
parent_task_id: TASK-18
priority: low
ordinal: 21000
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Lattice noise and the noise-driven textures from the book (noise/ is currently empty). Implement a LatticeNoise base with value and gradient noise, LinearNoise and CubicNoise interpolation, plus fractal sum (fBm) and turbulence. Then the noise textures: FBmTexture, TurbulenceTexture, WrappedFBmTexture, RampFBmTexture (marble look), and a wood texture. Built on the Texture abstraction from the infra subtask.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 A LatticeNoise implementation provides value/gradient noise plus fBm and turbulence
- [x] #2 fBm, turbulence, and a marble-style (ramp-fbm) texture render via an SV material
- [x] #3 Noise textures are declarable from the Builder DSL
- [x] #4 Unit tests cover noise determinism/range and the fBm/turbulence helpers
<!-- AC:END -->



## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Study 18.1/18.2 texture layer + detekt MagicNumber config (ignorePropertyDeclaration/ignoreConstantDeclaration on -> table consts/local vars are clean). DONE.
2. noise/LatticeNoise.kt (abstract, commonMain): fixed Perlin 256-entry permutation table (PERMUTATION_SIZE=256); DETERMINISTIC value table (seeded LCG, values in [-1,1]) and gradient table (seeded LCG -> unit vectors). Abstract valueNoise(p)/vectorNoise(p) (interpolation in subclass) wrapping the lattice index helper INDEX(ix,iy,iz). Concrete fbm/turbulence/fractalSum on top, with configurable numOctaves/lacunarity/gain. No unseeded Random.
3. noise/LinearNoise.kt: trilinear interpolation of the 8 corner lattice values/gradients.
4. noise/CubicNoise.kt: tricubic (Hermite/4-point) interpolation.
5. textures/FBmTexture, TurbulenceTexture, WrappedFBmTexture, RampFBmTexture (reuse 18.2 Ramp via colorAt), Wood. Each samples noise at sr.localHitPoint, lerps min/max colour (or ramp). data classes, Texture impls, pure colourAt seam where useful.
6. AC#3: confirm svMatte/svPhong/svEmissive already take Texture -> no DSL change; declare textures in example scenes.
7. commonTest noise tests: determinism (same point -> identical value, both LinearNoise & CubicNoise), range (value noise in [-1,1] over many sampled points), fbm bounds & turbulence>=0 over many points, more octaves add detail. Texture tests: FBm/Turbulence/RampFBm/Wood colour within min..max, determinism via testShade. Derive expectations from math.
8. examples/textures/noise/: marble (RampFBm) sphere + turbulence + wood scene(s) via svMatte. Render WHITTED/SEQUENTIAL/720p, verify visually.
9. just test green incl detekt; keep noise detekt-clean via consts/locals; report.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
Added noise/ package (commonMain): SeededRandom (deterministic LCG, no kotlin.random), LatticeNoise (abstract; fixed 256-entry Perlin permutation table; deterministic value table [-1,1] and unit-vector gradient table from seed 253; index() lattice hash; fractalSum/fbm, turbulence, configurable octaves/lacunarity/gain), LinearNoise (trilinear), CubicNoise (tricubic four-knot spline). Added textures (commonMain): FBmTexture, TurbulenceTexture, WrappedFBmTexture, RampFBmTexture (marble, reuses 18.2 Ramp.colorAt), Wood (concentric rings + turbulence warp). Each samples sr.localHitPoint; each has a pure colorFor/colorAt seam. All compile.
<!-- SECTION:NOTES:END -->
