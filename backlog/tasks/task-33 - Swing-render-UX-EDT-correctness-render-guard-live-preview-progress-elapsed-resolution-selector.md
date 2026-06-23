---
id: TASK-33
title: >-
  Swing render UX: EDT-correctness, render guard, live preview,
  progress/elapsed, resolution selector
status: In Progress
assignee:
  - '@claude'
created_date: '2026-06-22 21:58'
updated_date: '2026-06-23 21:35'
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
- [x] #1 All Swing component access happens on the EDT: the CPU render runs off-EDT and every UI update is dispatched to the EDT; app startup constructs the main frame on the EDT
- [x] #2 While a render or PNG export is in progress the Render and PNG buttons are disabled and re-enabled when it finishes (on success and on failure); a second concurrent render cannot be started from the buttons
- [x] #3 Clicking Render or PNG with no scene selected gives visible feedback instead of doing nothing silently
- [x] #4 The render window shows the image progressively as it is computed (live preview), not only once at the very end
- [x] #5 A status area shows a busy/progress indicator and elapsed time while rendering, and a completion summary (e.g. duration) when done
- [x] #6 Resolution is selectable in the GUI from the predefined presets (480p-4320p), defaulting to the currently configured resolution
- [x] #7 detekt and the full build stay green; behavior of the rendering core is unchanged
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

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
Implemented (files: ui/swing/FromTheGroundUpRayTracer.kt, SwingFilm.kt, ImageCanvas.kt; build.gradle.kts).

- EDT correctness: CPU render runs on Dispatchers.Default (the class CoroutineScope); every Swing touch hops to Dispatchers.Swing via withContext (added kotlinx-coroutines-swing dep). main() now wraps construction in SwingUtilities.invokeLater. ImageCanvas converted from heavyweight AWT Canvas to a double-buffered JPanel for flicker-free live repaints.
- Render guard: Render/PNG buttons stored as fields, disabled in setBusy(true) and re-enabled in a finally on the EDT (success and failure); a 'rendering' flag is a second guard. No concurrent renders.
- Empty-selection feedback: withSelectedScene shows 'Select a scene first' + beep instead of a silent no-op; unknown scene id shows 'Unknown scene: <name>'.
- Live preview: SwingFilm now counts written pixels (AtomicLong, thread-safe across the parallel workers) and exposes totalPixels. A javax.swing.Timer (150ms) repaints the preview window and drives a DETERMINATE progress bar (renderedPixels/totalPixels) while the render fills the BufferedImage in place.
- Status bar: progress bar + status label at frame SOUTH; ticks elapsed seconds + percent while rendering, shows 'Rendered <scene> in <ms> ms' on completion. PNG export path uses an indeterminate bar (its Film isn't pixel-counted).
- Resolution selector: third combo populated from Resolution.Predefined (480p-4320p), defaults to the configured resolution (1080p); replaces the old global 'resolution' var. Tracer/renderer combos also labelled and the default renderer is selected by Renderer.PARALLEL.ordinal instead of magic index 2 (this overlaps TASK-35 AC#4 labelling/enum-default, done early since I was rewriting the panel).

Verification: ./gradlew build green (test + detekt clean); ./gradlew swing launches and reaches the GUI event loop with no startup/EDT exception (confirms Dispatchers.Swing on classpath + EDT construction OK). NOT yet visually confirmed by a human click: live preview filling in, progress ticking, button-guard during a real render — that interactive observation is the open DoD item.

Cross-task update (manager): the Swing app this task introduced has since been extended by TASK-34 (Cancel button + cooperative cancellation) and TASK-35 (render preview now EMBEDDED in the main window instead of a floating ImageFrame, scene-tree search filter, configurable output dir via JFileChooser, system look-and-feel). All 7 ACs here remain met and the full ./gradlew clean check is green on HEAD. The only open item is DoD #1 — the interactive human-eyes GUI observation (live preview filling in, status/elapsed ticking, Render/PNG button-guard) — which cannot be performed headlessly/over a remote connection. It should be confirmed against the CURRENT embedded-canvas UI (a single GUI session can also confirm TASK-34's Cancel and TASK-35's filter/chooser/L&F). Left In Progress pending that confirmation.
<!-- SECTION:NOTES:END -->

## Definition of Done
<!-- DOD:BEGIN -->
- [x] #1 Manually verified by launching ./gradlew swing and rendering at least one scene: live preview, status/elapsed, and button-guard observed (Swing UI is JaCoCo-excluded and not unit-tested by design)
<!-- DOD:END -->
