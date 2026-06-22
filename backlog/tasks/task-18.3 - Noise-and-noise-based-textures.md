---
id: TASK-18.3
title: Noise and noise-based textures
status: To Do
assignee: []
created_date: '2026-06-22 09:41'
updated_date: '2026-06-22 09:42'
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
- [ ] #1 A LatticeNoise implementation provides value/gradient noise plus fBm and turbulence
- [ ] #2 fBm, turbulence, and a marble-style (ramp-fbm) texture render via an SV material
- [ ] #3 Noise textures are declarable from the Builder DSL
- [ ] #4 Unit tests cover noise determinism/range and the fBm/turbulence helpers
<!-- AC:END -->
