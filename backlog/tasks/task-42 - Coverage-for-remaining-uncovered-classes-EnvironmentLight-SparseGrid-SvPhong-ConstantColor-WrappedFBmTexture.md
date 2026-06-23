---
id: TASK-42
title: >-
  Coverage for remaining uncovered classes: EnvironmentLight, SparseGrid,
  SvPhong, ConstantColor, WrappedFBmTexture
status: In Progress
assignee:
  - '@claude'
created_date: '2026-06-23 19:15'
updated_date: '2026-06-23 19:47'
labels:
  - examples
  - coverage
dependencies: []
priority: low
ordinal: 45000
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
The TASK-38 audit lists a heterogeneous remainder of implemented-but-unexercised classes with no example scene: EnvironmentLight (light; environmentLight() already exists in LightsScope DSL), SparseGrid (acceleration structure), SvPhong (spatially-varying Phong material), and the textures ConstantColor and WrappedFBmTexture. Add example scene(s) covering them. Some may already be DSL-reachable (EnvironmentLight is); others (SparseGrid as an objects{} wrapper like grid/kdtree, the SvPhong material, the two textures) may need a DSL/materials check or a small builder. Confirm reachability per class and add the smallest wiring needed, then a scene. Scenes are coverage-excluded (verify by render); any commonMain wiring added is cover-first (frozen test).
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Example scene(s) instantiate EnvironmentLight, SparseGrid, SvPhong, ConstantColor, and WrappedFBmTexture
- [ ] #2 Re-running ./gradlew audit shows all five classes removed from their respective 'Uncovered classes' sections
- [ ] #3 Any new DSL/materials wiring added in commonMain to reach these classes is covered by a frozen unit test written before the scene
- [ ] #4 New scenes render to non-near-black images (verified manually) and are not flagged Suspect/Failed by the audit
- [ ] #5 Full build stays green: ./gradlew build (compile + test + detekt) passes
<!-- AC:END -->
