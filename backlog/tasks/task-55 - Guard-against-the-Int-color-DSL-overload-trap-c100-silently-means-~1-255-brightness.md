---
id: TASK-55
title: >-
  Guard against the Int-color DSL overload trap (c(1,0,0) silently means ~1/255
  brightness)
status: In Progress
assignee:
  - '@claude'
created_date: '2026-06-24 11:21'
updated_date: '2026-06-24 13:12'
labels:
  - tooling
  - dsl
  - examples
dependencies: []
priority: low
ordinal: 58000
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Discovered during TASK-43 (MultipleObjects.kt rendered ~100% near-black). The colors DSL has two overloads: c(Double,Double,Double) for 0.0..1.0 linear color and c(Int,Int,Int) for 0..255 (divides by 255). Writing a color with bare Int literals like c(1, 0, 0) binds to the Int overload and yields Color(0,0,0.0039) ~ 1/255 brightness instead of pure red, so a scene shades to near-black with no error. This is a silent footgun any scene author can hit (it was the true root cause of TASK-43, beyond the zero-intensity light). Decide and implement a guard. Options to weigh: (a) a detekt rule / custom lint that flags c(Int,Int,Int) calls where all args are 0/1 (the ambiguous range) or flags Int-literal calls in scene files; (b) rename/disambiguate the 0-255 overload (e.g. c255(...) or rgb255(...)) so the Double path is the obvious default and Int literals no longer silently bind; (c) extend the TASK-38 audit to specifically warn when a scene uses c(Int,...) with small values. Whatever is chosen, keep existing intended c(Int,...) 0-255 uses working or migrate them. Testable core covered cover-first; verify existing scenes still render identically.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 A clear mechanism prevents or flags the c(Int,Int,Int) near-black trap (detekt/lint rule, overload rename/disambiguation, or an audit warning) so authors cannot silently get ~1/255 colors from c(1,0,0)
- [ ] #2 Existing intended 0-255 color uses are preserved or migrated; no existing scene changes its rendered output unintentionally
- [ ] #3 Testable core covered by frozen cover-first tests; detekt and the full build stay green
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Cover-first: add frozen tests to WorldScopeTest pinning the surviving color factories — c(Double,Double,Double), c(Double), c(hex), and cInt(Int,Int,Int) (0-255). These pin the API that survives the change. 2. Remove the redundant/ambiguous c(Int,Int,Int) overload from WorldScope (keep cInt as the explicit 0-255 path), so bare-int c(1,0,0) becomes a COMPILE ERROR rather than silent ~1/255 near-black. 3. Migrate all 30 trap call sites (all use only 0/1 values) from c(1,0,0)-style to c(1.0,0.0,0.0)-style, producing the author's intended pure colors. 4. Update WorldScope KDoc referencing c/cInt. 5. Verify: ./gradlew clean check green + detekt clean; ./gradlew audit shows the migrated scenes go from near-black SUSPECT to correct (never the reverse).
<!-- SECTION:PLAN:END -->
