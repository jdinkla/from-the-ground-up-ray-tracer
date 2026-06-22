---
id: TASK-19
title: 'Realistic transparency: dielectrics and Fresnel'
status: In Progress
assignee:
  - '@claude'
created_date: '2026-06-22 09:41'
updated_date: '2026-06-22 14:58'
labels:
  - enhancement
  - book-parity
dependencies: []
priority: medium
ordinal: 22000
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
The repo has only simple/Whitted transparency (Transparent material + PerfectTransmitter BTDF). The book's realistic transparency is missing. Add a Dielectric material with separate inside/outside colored attenuation (Beer's law), a FresnelReflector BRDF and FresnelTransmitter BTDF that compute reflectance/transmittance from the Fresnel equations and the indices of refraction, and total-internal-reflection handling. The Whitted tracer must spawn correctly weighted reflected and transmitted rays. Self-contained relative to textures.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 A Dielectric material renders a glass-like object with refraction, total internal reflection, and colored attenuation through the medium
- [ ] #2 FresnelReflector (BRDF) and FresnelTransmitter (BTDF) compute reflectance/transmittance from indices of refraction
- [ ] #3 Dielectric is declarable from the Builder DSL with in/out IORs and filter colors
- [ ] #4 Unit tests cover Fresnel reflectance at normal and grazing incidence and the TIR threshold
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Add FresnelReflector (brdf/) and FresnelTransmitter (btdf/): compute exact unpolarized Fresnel kr/kt and refraction direction from etaIn/etaOut following Suffern ch.28. fresnel() returns reflectance; sampleF returns reflected/transmitted direction; isTir() true when radicand<0.
2. Add Dielectric material (materials/) extending Phong: shade() does Phong direct lighting + Fresnel-weighted reflected & transmitted recursion through world.tracer (mirrors Transparent.shade pattern, no tracer change). Apply Beer's-law colored attenuation exp(-c*t) using inside/outside filter colors chosen by normal.ray sign. areaLightShade analogous. NO change to Whitted tracer or existing materials.
3. DSL: add dielectric(...) to MaterialsScope taking iorIn/iorOut, cfIn/cfOut filter colors + Phong params, mirroring transparent()/reflective() idiom.
4. Unit tests (commonTest, StringSpec, shouldBeApprox): FresnelReflector reflectance at normal incidence (eta=1.5 -> 0.04 = ((eta-1)/(eta+1))^2), near-grazing -> ~1.0, TIR threshold for eta<1 past critical angle (isTir true, transmitter signals TIR). FresnelTransmitter direction sanity. MaterialsScope dielectric test.
5. Example scene under examples/materials/dielectric: glass Dielectric sphere over checkered/colored ground; render WHITTED/SEQUENTIAL 720p and verify refraction/TIR/attenuation visible (coverage-excluded -> manual verify).
6. just test green; keep new code detekt-clean via named consts/@Suppress. Verify Transparent + existing scenes unchanged.
<!-- SECTION:PLAN:END -->
