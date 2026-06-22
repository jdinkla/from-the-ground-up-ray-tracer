---
id: TASK-19
title: 'Realistic transparency: dielectrics and Fresnel'
status: In Progress
assignee:
  - '@claude'
created_date: '2026-06-22 09:41'
updated_date: '2026-06-22 14:56'
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
