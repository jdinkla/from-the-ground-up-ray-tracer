---
id: TASK-31
title: >-
  Fix Sampler.sampleUnitSquare IndexOutOfBounds for non-square sample counts
  (MultiJittered/Jittered/Regular)
status: In Progress
assignee:
  - '@claude'
created_date: '2026-06-22 21:31'
updated_date: '2026-06-23 20:15'
labels:
  - bug
  - samplers
dependencies: []
priority: medium
ordinal: 34000
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Discovered during TASK-30 (independently confirmed by review): Sampler.sampleUnitSquare() indexes its point array assuming numSamples*numSets generated points, but the sqrt-based generators MultiJittered, Jittered, and Regular only generate floor(sqrt(numSamples))^2 * numSets points. Consequences (verified by probe across generators x counts x sets): Sampler(MultiJittered, n, sets).sampleUnitSquare() throws IndexOutOfBoundsException for any NON-SQUARE n (e.g. n=20), AND MultiJittered additionally throws whenever numSets > sqrt(n) (its per-set stride uses p*numSets instead of p*n*n); Jittered throws for non-square n. Only NRooks and PureRandom are index-safe for arbitrary numSamples. This is why the historically-dead SampledSingleRayRenderer's Sampler(MultiJittered, 2500, 10) survived (2500=50^2, 50>=10), and why TASK-30 had to sidestep the bug by using NRooks for the in-pixel anti-aliasing sampler. Fix the index math so all generators work for arbitrary (numSamples, numSets): either generate exactly numSamples*numSets points (round numSamples up to a perfect square for the sqrt-based generators, or distribute correctly) or make sampleUnitSquare index by the actual generated count; and fix MultiJittered's per-set stride (p*numSets -> p*n*n or equivalent). Cover-first: add tests asserting every generator yields a valid in-[0,1)^2 sample for a range of non-square numSamples and numSets > sqrt(numSamples) without throwing, and that distributions are still stratified. Once fixed, TASK-30's renderer/lens samplers can use MultiJittered (better decorrelation) instead of the NRooks workaround.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Sampler.sampleUnitSquare returns a valid sample in [0,1)^2 for every generator (MultiJittered/Jittered/Regular/NRooks/PureRandom) across non-square numSamples and numSets > sqrt(numSamples), with no IndexOutOfBounds
- [ ] #2 MultiJittered/Jittered/Regular generate the number of points sampleUnitSquare expects (or sampleUnitSquare indexes by the actual count); MultiJittered per-set stride corrected
- [ ] #3 Cover-first tests across generators x non-square counts x set counts; existing sampler-dependent behavior (AmbientOccluder, AreaLight, path tracing) unaffected; full suite + detekt green
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Read Sampler + generators (MultiJittered/Jittered/Regular/NRooks/PureRandom) and Sampler indexing math. 2. Cover-first: add SamplerIndexingTest exercising sampleUnitSquare/Disk/Hemisphere/Sphere for all generators x non-square numSamples x numSets>sqrt(n); confirm it FAILS (IndexOutOfBounds) before fix. 3. Fix Sampler to derive per-set stride from the actual generated sample count (samples.size/numSets) instead of the requested numSamples, so it works for every generator. 4. Fix MultiJittered per-set stride bug (p*numSets -> p*n*n) and its exact allocation. 5. Run new test green, then full ./gradlew build (existing sampler-dependent tests + detekt).
<!-- SECTION:PLAN:END -->
