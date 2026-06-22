---
id: TASK-18
title: Texture and mapping support (book parity)
status: To Do
assignee: []
created_date: '2026-06-22 09:41'
labels:
  - enhancement
  - book-parity
dependencies: []
priority: medium
ordinal: 18000
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Umbrella for the book's texture system, which is entirely absent here (textures/, noise/, mappings/ packages are empty). Suffern devotes several chapters to texturing: a Texture abstraction sampled during shading to produce a color, spatially varying materials (SV_Matte/SV_Phong) that read a Texture instead of a constant diffuse color, image textures with Mapping classes that project a 2D image onto 3D objects (sphere, rectangle, light-probe/panoramic), procedural textures, and noise-based textures. This is goal (b) in CLAUDE.md, called out explicitly. Delivered via the subtasks; this parent tracks the overall feature and its example scenes.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Objects can be assigned a texture-driven material via the existing Builder DSL
- [ ] #2 All subtasks are completed and at least one example scene per texture family renders correctly
<!-- AC:END -->
