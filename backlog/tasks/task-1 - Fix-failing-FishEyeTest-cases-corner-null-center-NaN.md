---
id: TASK-1
title: Fix failing FishEyeTest cases (corner null + center NaN)
status: In Progress
assignee:
  - '@claude'
created_date: '2026-06-22 09:05'
updated_date: '2026-06-22 10:13'
labels: []
dependencies: []
priority: high
ordinal: 1000
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Four FishEyeTest cases fail on both JDK 21 (CI, run 27940637964) and JDK 25, so the build (clean check) is red on main. Introduced by commit 337da7d 'Add Kotest unit tests for cameras/lenses'. Two genuine JDK-independent bugs in src/commonMain/kotlin/net/dinkla/raytracer/cameras/lenses/FishEye.kt: (1) the else branch at FishEye.kt:75 returns RayDirection(Vector3D.ZERO) and drops rSquared, so the 'rSquared <= 1' null-guard reads the default 0.0 and corner pixels that should yield null return a zero-direction ray (fails FishEyeTest.kt:50 and :64); (2) at the exact view-plane center r = sqrt(0) = 0, so sinAlpha=y/r and cosAlpha=x/r are 0/0 = NaN, making the direction NaN (fails normalization at FishEyeTest.kt:43 and the sampled-vs-single match at :75). Fix the lens math so corners return null and the center yields a normalized forward ray; do not weaken the tests.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 getRaySingle and getRaySampled return null for pixels whose rSquared > 1 (corners), e.g. (0,0)
- [ ] #2 At the view-plane center the ray direction is finite and normalized (length approx 1.0), pointing straight forward
- [ ] #3 getRaySampled with a zero sample matches getRaySingle at the center
- [ ] #4 All FishEyeTest cases pass and 'just test' (gradle clean check) is green
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. FishEye.getRayDirection: propagate rSquared in the out-of-circle else branch so the caller's 'rSquared <= 1' null-guard correctly rejects corners. 2. Guard the center case (r==0) so sinAlpha/cosAlpha don't divide by zero; center yields a finite, normalized forward direction (-w). 3. All FishEyeTest cases pass; just test green.
<!-- SECTION:PLAN:END -->
