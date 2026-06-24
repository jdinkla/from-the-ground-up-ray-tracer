---
id: TASK-56
title: >-
  Fix salt-and-pepper noise in Bunny render (ambient occluder sampled at 1
  ray/point)
status: In Progress
assignee: []
created_date: '2026-06-24 16:56'
updated_date: '2026-06-24 17:07'
labels:
  - bug
dependencies: []
priority: medium
ordinal: 59000
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
The Bunny.kt scene renders with scattered fully-black pixels (salt-and-pepper noise) across the bunny surface AND the floor plane — see renders/20260624153703_Bunny.png and the zoomed crops.

Root cause: the scene declares `ambientOccluder(sampler = sampler, numSamples = 1)` (Bunny.kt:28). In AmbientOccluder.l(), `ratio = 1.0 - numHits / numSamples`; with numSamples = 1 the occlusion estimate is BINARY (0.0 or 1.0) per shading point. Each point gets either full ambient or zero ambient from a single occlusion ray. Where that one AO ray hits the bunny and the point light is also blocked/back-facing, the pixel goes fully black — hence the speckle on both the bunny and the floor near the contact region. The scene configures a rich 2500-sample MultiJittered sampler but then draws exactly one ray from it.

Not a correctness bug (no self-intersection / kd-tree epsilon issue) and not the point-light shadow (which is clean). It is the AO integral estimated with a single binary sample.

Evidence from sibling scenes: AmbientOccludedSphere.kt uses numSamples = 32; World61.kt uses NUM_AMBIENT_SAMPLES = 4. Bunny's value of 1 is the outlier.

Fix: raise Bunny's numSamples to a sane value (recommend ~64 given the scene comment 'This can take longer' signals quality intent; 32 is the proven value from AmbientOccludedSphere). No production-core change required.

Note (out of scope, context only): AmbientOccluder takes both a Sampler (with its own numSamples) and a separate numSamples that decouples how many rays are cast from the sampler's set size — a footgun that made numSamples=1 easy to write. Worth a follow-up if it recurs, but not part of this fix.

This change lives in examples/** which JaCoCo excludes (per CLAUDE.md), so verify by re-rendering rather than adding a unit test.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Bunny.kt ambient occluder numSamples is raised from 1 to a value consistent with the other AO scenes (>= ~16; recommended ~64)
- [x] #2 A fresh Bunny render shows no scattered fully-black pixels on the bunny surface or the floor plane (ambient occlusion reads as smooth soft shadowing)
- [x] #3 The point-light hard shadow and overall composition are unchanged; only the ambient noise is removed
- [x] #4 Verified manually by re-rendering (examples/** is coverage-excluded; no unit test added) and the new render path is noted in the task
<!-- AC:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
Fix: Bunny.kt:28 ambientOccluder numSamples 1 -> 64 (single line; examples/** is JaCoCo-excluded, so no unit test). Verified by re-render: ./gradlew run --args="--world=Bunny.kt --tracer=WHITTED --renderer=FORK_JOIN --resolution=480p". New render: renders/20260624170029_Bunny.png. Salt-and-pepper black dropouts are gone on both the bunny surface and the floor; AO now reads as smooth soft darkening in the recesses; point-light hard shadow and composition unchanged. Cost note: render took ~342s at 480p (AO drove ~14.5M shadow rays vs. a handful at numSamples=1) — consistent with the scene's 'This can take longer' comment. Not committed (left for the usual commit/work-board flow).
<!-- SECTION:NOTES:END -->

## Final Summary

<!-- SECTION:FINAL_SUMMARY:BEGIN -->
Raised Bunny.kt ambient-occluder numSamples from 1 to 64. Root cause was a binary AO estimate (ratio = 1 - numHits/numSamples with a single sample) producing scattered fully-black pixels on the bunny and floor. Verified by re-rendering at 480p (renders/20260624170029_Bunny.png): noise eliminated, point-light shadow and composition intact.
<!-- SECTION:FINAL_SUMMARY:END -->
