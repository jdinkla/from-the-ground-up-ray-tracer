---
id: TASK-22
title: Stereo camera
status: In Progress
assignee:
  - '@claude'
created_date: '2026-06-22 09:41'
updated_date: '2026-06-22 17:44'
labels:
  - enhancement
  - book-parity
dependencies: []
priority: low
ordinal: 25000
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
The book's StereoCamera (parallel and transverse/anaglyph stereo, rendering left/right eye views) is the one camera type missing here (Pinhole, ThinLens, FishEye, Spherical exist). Add it as a camera/lens that renders a side-by-side or anaglyph stereo pair. Small, self-contained parity item.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 A stereo camera renders a left/right eye pair (side-by-side and/or anaglyph) for a scene
- [ ] #2 Stereo camera is selectable from the Builder DSL
<!-- AC:END -->
