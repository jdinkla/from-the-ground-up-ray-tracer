---
id: TASK-23
title: 'Fix solveQuartic: returns wrong roots (quadric-root clobbering)'
status: In Progress
assignee:
  - '@claude'
created_date: '2026-06-22 10:04'
updated_date: '2026-06-22 10:13'
labels:
  - bug
  - math
dependencies: []
priority: high
ordinal: 26000
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Polynomials.solveQuartic returns mathematically wrong roots for many quartics. Discovered because PolynomialsTest had a no-assertion (println-only) quartic case hiding it.

Root cause (Polynomials.kt:180-192): the C++ original appended the second quadric's roots via pointer arithmetic (solve_quadric(coeffs, s + num)). The Kotlin port replaced that with temp arrays and writes the second pair back to s[0]/s[1] (and reads ss3 = [s[0+num], s[1+num]]), clobbering the first quadric's roots and mismanaging the offset. Net effect: the first pair of real roots is lost.

Evidence (faithful port reproduction):
- 1.2 - 3.2x + 1.7x^2 + 2.5x^3 - 1.02x^4 : true roots ~ -1.2875, 2.6976; returns 0.6127 twice (the -A/4 offset).
- x^4 - 5x^2 + 4 (roots +-1, +-2): returns [2, 1, -2.5, 0].
- x^4 - 1 (roots +-1): returns [0, 0].
- (x^2-4)^2 (double +-2): returns [2, 0].
Degenerate cases like x^4 - x^2 and x^4 happen to work, which is why torus rendering (positive leading coeff, specific paths) has not obviously broken.

Note: PolynomialsTest now contains a characterization test pinning the WRONG output for the 1.2..-1.02 case (labelled, referencing this task). When fixed, that test must be updated to assert the correct roots.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 solveQuartic returns the correct real roots for x^4-5x^2+4 (+-1, +-2), x^4-1 (+-1), and (x^2-4)^2 (double +-2)
- [ ] #2 Every returned root satisfies the polynomial within tolerance (residual ~ 0) across a range of test quartics, including 4 distinct real roots and negative leading coefficients
- [ ] #3 The characterization test in PolynomialsTest pinning the wrong 1.2..-1.02 output is replaced with an assertion of the correct roots
- [ ] #4 Torus ray-intersection behavior is unchanged or improved (TorusTest still green)
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Polynomials.solveQuartic else-branch: write the second quadric's roots at offset s[num] (not s[0]/s[1]), restoring the C++ 's + num' append semantics. 2. Replace the characterization test (pinned wrong roots) with correctness assertions: roots satisfy the polynomial; add a 4-distinct-root regression test. 3. just test green.
<!-- SECTION:PLAN:END -->
