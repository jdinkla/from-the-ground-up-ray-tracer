---
id: TASK-33
title: >-
  Swing render UX: EDT-correctness, render guard, live preview,
  progress/elapsed, resolution selector
status: To Do
assignee: []
created_date: '2026-06-22 21:58'
labels:
  - swing
  - ui
dependencies: []
priority: high
ordinal: 36000
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
The Swing desktop app touches Swing components off the Event Dispatch Thread during and after renders (ImageFrame construction, repaint, dialogs, and main()), gives no feedback while a possibly minutes-long render runs, lets the user launch unbounded concurrent renders, silently does nothing when no scene is selected, and cannot set resolution from the GUI even though the CLI can. Make the desktop app correct and responsive WITHOUT touching the renderer core (cancellation is split into a separate task because it needs cancellation-aware renderers).
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 All Swing component access happens on the EDT: the CPU render runs off-EDT and every UI update is dispatched to the EDT; app startup constructs the main frame on the EDT
- [ ] #2 While a render or PNG export is in progress the Render and PNG buttons are disabled and re-enabled when it finishes (on success and on failure); a second concurrent render cannot be started from the buttons
- [ ] #3 Clicking Render or PNG with no scene selected gives visible feedback instead of doing nothing silently
- [ ] #4 The render window shows the image progressively as it is computed (live preview), not only once at the very end
- [ ] #5 A status area shows a busy/progress indicator and elapsed time while rendering, and a completion summary (e.g. duration) when done
- [ ] #6 Resolution is selectable in the GUI from the predefined presets (480p-4320p), defaulting to the currently configured resolution
- [ ] #7 detekt and the full build stay green; behavior of the rendering core is unchanged
<!-- AC:END -->

## Definition of Done
<!-- DOD:BEGIN -->
- [ ] #1 Manually verified by launching ./gradlew swing and rendering at least one scene: live preview, status/elapsed, and button-guard observed (Swing UI is JaCoCo-excluded and not unit-tested by design)
<!-- DOD:END -->
