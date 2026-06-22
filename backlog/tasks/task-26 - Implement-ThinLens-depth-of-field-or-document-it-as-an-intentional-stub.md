---
id: TASK-26
title: Implement ThinLens depth-of-field (or document it as an intentional stub)
status: To Do
assignee: []
created_date: '2026-06-22 14:20'
labels:
  - bug
  - camera
dependencies: []
priority: low
ordinal: 29000
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
ThinLens.getDirection ignores the pixel coordinates, the lens sample point, focal distance f, lens radius d, and the sampler, always returning a fixed pm(1,1,1) ray -- it does not implement the book's thin-lens depth-of-field model (Suffern ch. 10). Either implement proper thin-lens DoF (sample the lens disk, compute the focal-plane hit, build the ray from the lens point to the focal point) or, if DoF is intentionally out of scope, document ThinLens as a stub and curtail its CLI/scene exposure so it does not silently produce a pinhole-like image. Discovered while working TASK-13.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 ThinLens either produces depth-of-field rays per the book's model, or is clearly documented as a non-functional stub
- [ ] #2 If implemented: a scene/test demonstrates focal-plane focus with blur outside the focal plane
- [ ] #3 If documented as a stub: code KDoc and any user-facing help reflect that it does not blur
<!-- AC:END -->
