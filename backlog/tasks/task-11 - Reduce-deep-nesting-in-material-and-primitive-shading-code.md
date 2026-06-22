---
id: TASK-11
title: Reduce deep nesting in material and primitive shading code
status: In Progress
assignee:
  - '@claude'
created_date: '2026-06-22 09:11'
updated_date: '2026-06-22 12:32'
labels:
  - refactor
dependencies: []
references:
  - docs/arc42/11_risks_and_technical_debt.md
priority: medium
ordinal: 11000
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Eight blocks exceed the nesting depth threshold of 4, notably Matte.kt areaLightShade (66), Phong.kt areaLightShade (77), and OpenCylinder.kt hit (29). Apply early returns and extract nested blocks into helper methods to flatten control flow.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 No block exceeds nesting depth 4 in the affected methods
- [ ] #2 Shading/intersection behavior unchanged; tests pass
<!-- AC:END -->
