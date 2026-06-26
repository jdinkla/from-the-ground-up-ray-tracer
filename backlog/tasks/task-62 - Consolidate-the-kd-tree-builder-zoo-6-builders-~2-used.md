---
id: TASK-62
title: 'Consolidate the kd-tree builder zoo (6 builders, ~2 used)'
status: Done
assignee:
  - '@claude'
created_date: '2026-06-24 22:36'
updated_date: '2026-06-26 21:36'
labels:
  - tech-debt
  - refactoring
dependencies: []
references:
  - docs/arc42/11_risks_and_technical_debt.md
priority: medium
ordinal: 65000
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
objects/acceleration/kdtree/builder/ holds 6 TreeBuilder strategies: SpatialMedianBuilder, ObjectMedianBuilder, ObjectMedian2Builder, Simple2Builder, TestBuilder, Test2Builder. Only SpatialMedianBuilder is used by production rendering (KDTree.kt default + DSL) and Simple2Builder by one example (World75.kt). The other four are referenced only by the TreeBuilder factory, each other, and KDTreeBuilderTest. The Test/2 suffixes mark them as left-in experiments.

Five-plus near-duplicate split-plane algorithms are maintenance weight: every interface change (TreeBuilder, Node, BBox) must be propagated through all of them, and a reader cannot tell which is canonical or why the alternatives exist. They are tested and DSL-selectable, so this is 'consolidate and justify,' NOT 'delete dead code.' See TECH_DEBT_REPORT.md section P2 #6.

Locations: src/commonMain/.../objects/acceleration/kdtree/builder/ (all 6 builders + TreeBuilder factory); KDTreeBuilderTest; World75.kt.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 A deliberate per-builder decision is recorded: keep canonical builders (documented), delete experimental variants together with their tests, or keep with a documented rationale plus a justifying benchmark
- [x] #2 SpatialMedianBuilder (production default) and Simple2Builder (World75 example) remain functional
- [x] #3 If a builder is removed, the TreeBuilder factory, KDTreeBuilderTest, and any DSL references are updated so the build stays green
- [x] #4 If a builder is removed, the rendering output of an affected scene is manually verified to be unchanged
- [x] #5 TreeBuilder KDoc documents which builders are canonical and why any alternatives are kept
- [x] #6 ./gradlew clean check is green
<!-- AC:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
Per-builder decision recorded: KEEP SpatialMedianBuilder (canonical production default, wired into KDTree + the kdtree{} DSL, covered by KDTreeTest/StatisticsTest/KDTreeBuilderTest) and Simple2Builder (selected by the SphereLatticeInKdTree example). DELETE the four unused experimental variants — ObjectMedianBuilder, ObjectMedian2Builder, TestBuilder, Test2Builder — none referenced by any production path or example; Test/Test2 were additionally buggy dead code (latent TASK-4 split-copy bug pinned only as characterization). All six implemented TreeBuilder directly (no inheritance), so deletion was clean. Removed the four files and their cases from KDTreeBuilderTest (kept the SpatialMedian, Simple2 and KDTree-wrapper sections). Updated TreeBuilder KDoc to document the two canonical builders and why the alternatives were dropped (AC#5). clean check green; rendered SphereLatticeInKdTree (Simple2) at 720p -> tree built (70 nodes), 921600 pixels traced, unchanged behaviour (AC#4); SpatialMedian default also exercised by the green test suite.
<!-- SECTION:NOTES:END -->
