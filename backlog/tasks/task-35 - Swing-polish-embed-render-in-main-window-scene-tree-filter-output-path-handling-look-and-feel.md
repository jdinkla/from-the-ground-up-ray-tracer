---
id: TASK-35
title: >-
  Swing polish: embed render in main window, scene-tree filter, output-path
  handling, look-and-feel
status: To Do
assignee: []
created_date: '2026-06-22 21:58'
labels:
  - swing
  - ui
dependencies: []
priority: low
ordinal: 38000
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Lower-priority quality-of-life improvements for the Swing desktop app, surfaced during the TASK-33 review. Independent of the correctness/UX work in TASK-33 and the cancellation work in its sibling task.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Render results are presented in the main window (e.g. embedded panel or tab) instead of each render spawning a separate floating ImageFrame that piles up
- [ ] #2 The scene list (currently a flat JTree of every example file) supports filtering/search or grouping so a specific scene is quick to find
- [ ] #3 Render/PNG output location is predictable and configurable (e.g. a chosen output directory / JFileChooser) instead of hardcoded '../' relative to the working directory
- [ ] #4 Combo boxes are labelled (tracer vs renderer vs resolution) and the default renderer is selected by enum value rather than a magic index; system look-and-feel is applied
- [ ] #5 Code smells addressed: LeftSide no longer extends Component as a mere holder; the ImageFrame title-bar height fudge is removed or justified
<!-- AC:END -->
