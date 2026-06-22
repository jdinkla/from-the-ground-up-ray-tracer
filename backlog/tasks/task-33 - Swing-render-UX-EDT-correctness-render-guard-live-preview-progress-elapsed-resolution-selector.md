---
id: TASK-33
title: >-
  Swing render UX: EDT-correctness, render guard, live preview,
  progress/elapsed, resolution selector
status: In Progress
assignee:
  - '@claude'
created_date: '2026-06-22 21:58'
updated_date: '2026-06-22 21:59'
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

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Add kotlinx-coroutines-swing dependency for clean Dispatchers.Swing EDT dispatch.
2. SwingFilm: expose a thread-safe pixel-write counter (volatile/AtomicLong) so the UI can show progress; keep core IFilm contract unchanged.
3. ImageFrame/ImageCanvas: support live repaint while pixels stream in.
4. FromTheGroundUpRayTracer: run CPU render on Dispatchers.Default, hop to Dispatchers.Swing (withContext) for every Swing touch; guard Render/PNG buttons (disable during render, re-enable in finally); show visible feedback on empty selection; add a status bar (busy progress bar + elapsed time + completion summary) driven by a javax.swing.Timer that repaints the preview live; add a resolution selector combo populated from Resolution.Predefined defaulting to current config.
5. main(): wrap construction in SwingUtilities.invokeLater (EDT).
6. Verify: ./gradlew detekt + build green; launch ./gradlew swing and render a scene to confirm live preview, status, button-guard.
<!-- SECTION:PLAN:END -->

## Definition of Done
<!-- DOD:BEGIN -->
- [ ] #1 Manually verified by launching ./gradlew swing and rendering at least one scene: live preview, status/elapsed, and button-guard observed (Swing UI is JaCoCo-excluded and not unit-tested by design)
<!-- DOD:END -->
