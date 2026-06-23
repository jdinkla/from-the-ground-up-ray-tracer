---
id: TASK-35
title: >-
  Swing polish: embed render in main window, scene-tree filter, output-path
  handling, look-and-feel
status: In Progress
assignee:
  - '@claude'
created_date: '2026-06-22 21:58'
updated_date: '2026-06-23 21:08'
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
- [x] #1 Render results are presented in the main window (e.g. embedded panel or tab) instead of each render spawning a separate floating ImageFrame that piles up
- [x] #2 The scene list (currently a flat JTree of every example file) supports filtering/search or grouping so a specific scene is quick to find
- [x] #3 Render/PNG output location is predictable and configurable (e.g. a chosen output directory / JFileChooser) instead of hardcoded '../' relative to the working directory
- [x] #4 Combo boxes are labelled (tracer vs renderer vs resolution) and the default renderer is selected by enum value rather than a magic index; system look-and-feel is applied
- [x] #5 Code smells addressed: LeftSide no longer extends Component as a mere holder; the ImageFrame title-bar height fudge is removed or justified
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. AC#4: apply system L&F in main() on the EDT before building the UI (UIManager.setLookAndFeel).
2. AC#1: embed the live-preview canvas in the main window. Replace floating ImageFrame with an embedded ImageCanvas held by the controller (in a scrollable preview pane in the center). startInteractiveRender repaints the embedded canvas; preserve SwingFilm pixel counter + Timer + progressBar + status/elapsed + Cancel flow unchanged. Remove ImageFrame.
3. AC#2: add a search JTextField above the scene tree that filters the tree by substring (rebuild filtered tree model). Keep tree selection -> source load behavior.
4. AC#3: make PNG output directory user-choosable via JFileChooser with a sensible default (user.dir 'output' subdir, created if missing), replacing hardcoded '../'. Apply to both interactive-render PNG save and the PNG button path.
5. AC#5: LeftSide becomes a plain class exposing the JScrollPane (no longer extends Component); ImageFrame title-bar fudge moot because ImageFrame removed (note it).
6. Verify: ./gradlew build green (detekt covers this code, define MagicNumber constants), launch ./gradlew swing headlessly checking for no startup/EDT exceptions; document preserved render-flow trace.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
Partial overlap already landed in TASK-33: AC#4's combo labelling (Tracer/Renderer/Resolution labels) and selecting the default renderer by Renderer.PARALLEL.ordinal (not magic index 2) are DONE. Remaining for AC#4 here: apply the system look-and-feel. ACs #1 (embed render in main window), #2 (scene-tree filter), #3 (output-path handling), #5 (LeftSide/ImageFrame smells) are untouched.

TASK-35 implemented in ui/swing/** only (no renderer-core/example changes).

AC#1 (embed render in main window): removed the floating ImageFrame; the live preview is now an embedded ImageCanvas held by the controller, hosted in a JScrollPane in the right inner split-pane of the main window. ImageCanvas gained a settable 'image' var so each render installs its fresh SwingFilm.image and the canvas is reused (no window pile-up). startInteractiveRender now does previewCanvas.image = film.image and the javax.swing.Timer repaints previewCanvas (was imageFrame.repaint()). TASK-33/34 flow preserved: Render -> off-EDT launch on Dispatchers.Default -> startInteractiveRender builds world off-EDT then on Swing installs image + starts Timer(150ms) -> Timer reads SwingFilm.renderedPixels/totalPixels (AtomicLong) to drive progressBar + status/elapsed and repaints the embedded canvas live -> Render.render(film,renderer,token) runs off-EDT -> on success repaint+100% on EDT, save PNG; token.isCancelled handled; CancellationException caught before generic Exception; NonCancellable finally stops Timer + setBusy(false) restores idle UI. Cancel (onCancel) still flips token + cancels job.

AC#2 (scene-tree filter): LeftSide now has a Search JTextField above the JTree; a DocumentListener filters the file-name list case-insensitively and rebuilds the tree model (expanding rows), so a scene is quick to find. Tree-selection -> source load unchanged.

AC#3 (output path): added an editable 'Output:' JTextField + 'Choose…' JFileChooser (DIRECTORIES_ONLY). Default is a predictable renders/ folder under user.dir (created via mkdirs), replacing the hardcoded '../'. Both the interactive-render PNG save and the PNG-button path now write via outputPath(name); the field is read on the EDT.

AC#4 (look-and-feel): main() now calls UIManager.setLookAndFeel(getSystemLookAndFeelClassName()) inside SwingUtilities.invokeLater (on the EDT) before constructing the UI, wrapped in runCatching to fall back gracefully. (Combo labelling + Renderer.PARALLEL.ordinal default already landed in TASK-33.)

AC#5 (smells): LeftSide no longer extends Component — it is a plain holder composing widgets, exposing component:JPanel and tree:JTree. ImageFrame.kt deleted entirely, so its +22 title-bar height fudge is moot (noted). MagicNumber constants added (OUTPUT_FIELD_COLUMNS, SPLIT_RESIZE_WEIGHT, INITIAL_DIVIDER, SEARCH_BORDER, EMPTY_CANVAS_SIZE).

Verification: 'just test' (= ./gradlew clean check: compile + all tests + detekt) GREEN. ./gradlew build GREEN. ./gradlew swing launched, reached '> Task :swing' (app run-task blocks while GUI alive), stayed up 8s+, log clean — no startup/EDT exception, no stack traces (only the benign Gradle task name :checkKotlinGradlePluginConfigurationErrors matched an 'Error' substring). NOT verifiable headlessly: a human watching the live preview fill in and clicking Cancel — needs the user's eyes (open DoD item).

Post-review cleanup: removed the dead outputField.addActionListener (option a) — outputPath() always re-reads outputField.text directly, so the listener's write to outputDirectory was never observed. The outputDirectory field stays meaningful: it seeds the JFileChooser starting dir and is written by chooseOutputDirectory()/outputPath(); the editable field remains the single source of truth (hand-typed paths still honoured). Re-ran ./gradlew clean check: BUILD SUCCESSFUL (green); only the two pre-existing unrelated unchecked-cast warnings remain.
<!-- SECTION:NOTES:END -->
