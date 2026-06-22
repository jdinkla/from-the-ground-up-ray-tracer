---
id: TASK-18.2
title: Procedural textures
status: In Progress
assignee:
  - '@claude'
created_date: '2026-06-22 09:41'
updated_date: '2026-06-22 14:45'
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
