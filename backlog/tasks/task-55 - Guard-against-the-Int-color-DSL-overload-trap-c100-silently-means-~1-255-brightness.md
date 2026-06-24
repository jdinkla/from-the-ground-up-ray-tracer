---
id: TASK-55
title: >-
  Guard against the Int-color DSL overload trap (c(1,0,0) silently means ~1/255
  brightness)
status: Done
assignee:
  - '@claude'
created_date: '2026-06-24 11:21'
updated_date: '2026-06-24 13:23'
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
- [x] #1 A clear mechanism prevents or flags the c(Int,Int,Int) near-black trap (detekt/lint rule, overload rename/disambiguation, or an audit warning) so authors cannot silently get ~1/255 colors from c(1,0,0)
- [x] #2 Existing intended 0-255 color uses are preserved or migrated; no existing scene changes its rendered output unintentionally
- [x] #3 Testable core covered by frozen cover-first tests; detekt and the full build stay green
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Cover-first: add frozen tests to WorldScopeTest pinning the surviving color factories — c(Double,Double,Double), c(Double), c(hex), and cInt(Int,Int,Int) (0-255). These pin the API that survives the change. 2. Remove the redundant/ambiguous c(Int,Int,Int) overload from WorldScope (keep cInt as the explicit 0-255 path), so bare-int c(1,0,0) becomes a COMPILE ERROR rather than silent ~1/255 near-black. 3. Migrate all 30 trap call sites (all use only 0/1 values) from c(1,0,0)-style to c(1.0,0.0,0.0)-style, producing the author's intended pure colors. 4. Update WorldScope KDoc referencing c/cInt. 5. Verify: ./gradlew clean check green + detekt clean; ./gradlew audit shows the migrated scenes go from near-black SUSPECT to correct (never the reverse).
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
INVESTIGATION: c(Int,Int,Int) and cInt(Int,Int,Int) are byte-for-byte identical (both divide by 255). cInt already exists as the explicit, intended 0-255 factory (used 14x in World74/World74kdt with genuine 0-255 values like cInt(111,148,205)). ALL 30 c(Int,Int,Int) literal call sites use only 0/1 values => every one is the trap (author wanted pure color, got ~1/255 near-black). ZERO legitimate 0-255 c(Int,...) literal uses exist. detekt does NOT scan src/examples (build.gradle.kts source set list), so a custom detekt rule (option c) cannot see the trap. CHOSE option (b): remove the ambiguous overload, keep cInt; migrate the 30 trap sites to Double literals. Rationale: it is the root fix (compile error, not after-the-fact warning), completes the cInt design intent already in the codebase, bounded known blast radius.

IMPLEMENTED option (b). Files changed: (1) WorldScope.kt — removed the redundant/ambiguous c(Int,Int,Int) overload (kept cInt as the explicit 0-255 path); now bare-int c(1,0,0) is a COMPILE ERROR, not silent ~1/255 near-black. Updated class + cInt KDoc to document the trap and the fix. (2) WorldScopeTest.kt — cover-first frozen tests pinning the surviving factories: 'c(Double,Double,Double) builds a linear-space colour from its components verbatim', 'c(Double) builds a grey colour with all three channels equal', 'cInt builds a colour by dividing each 0-255 channel by 255', 'the explicit 0-255 path of a small triple stays near-black, unlike the Double pure colour'. These passed against the UNREFACTORED code before the overload was removed. (3) 30 trap call sites migrated across 10 scene files (Bunny, DepthOfFieldDemo, DepthOfFieldDemoSharp, World58, World60, World27, World16, World26, World30, World28): each c(i,i,i) with 0/1 args -> c(i.0,i.0,i.0), producing the author's intended pure colour. cInt 0-255 uses (World74/World74kdt) untouched.

AC#2 JUDGMENT CALL flagged for reviewer: the 30 migrated sites were ALL the trap (every literal used only 0/1) and were rendering near-black surfaces. Migrating fixes them to the manifest intended colour (e.g. World28 material 'red' now renders pure red, not near-black). So those scenes' surfaces DO change output — but this is the intended bug fix (the TASK-43 root cause), not unintended drift. No scene went the other way (correct->black). Verified via ./gradlew audit: identical to baseline — 'No scene rendered (near-)black above the threshold'; only pre-existing World61.kt missing-mesh failure remains. Manually rendered World28 at 720p (examples is a coverage-excluded zone): coherent image, the previously-near-black red sphere now vivid pure red.

VERIFICATION: ./gradlew clean check = BUILD SUCCESSFUL (compile incl. examples srcDir, all tests, detekt all green; only pre-existing PlyReader/GridStructuresTest unchecked-cast warnings). ./gradlew audit = no new near-black, no new build failures.
<!-- SECTION:NOTES:END -->

## Final Summary

<!-- SECTION:FINAL_SUMMARY:BEGIN -->
Removed the ambiguous c(Int,Int,Int) DSL color overload from WorldScope (option b), so bare-int color literals like c(1,0,0) now FAIL TO COMPILE instead of silently producing ~1/255 near-black — a compile-time guard, the strongest option, and the root cause of TASK-43. The explicit cInt(Int,Int,Int) 0-255 factory already existed and remains as the documented 0-255 path (its legitimate uses in World74/World74kdt untouched). Investigation justified the choice: c() and cInt() were byte-identical, ALL 30 c(Int,Int,Int) literal call sites used only 0/1 args (every one was the trap, zero legitimate 0-255 literal uses), and detekt does not scan src/examples so a custom rule (option c) could not see the trap. Migrated all 30 sites across 10 example scenes (Bunny, DepthOfFieldDemo/Sharp, World58/60/27/16/26/30/28) from c(i,j,k) to c(i.0,j.0,k.0) so each renders the author's intended pure color (e.g. World28's material literally named 'red' now renders pure red). Added cover-first frozen WorldScopeTest cases pinning the surviving factories (c(Double..), c(Double), cInt, and that the 0-255 small-triple stays near-black). Verified: ./gradlew clean check + detekt green; reviewer PASS (confirmed overload removal complete via green build + zero remaining trap grep, all migrations are c(i.0,..) not cInt (so the bug is fixed not preserved), legitimate cInt uses untouched, judgment call sound); ./gradlew audit identical to baseline with no near-black scene. The appearance change to the 10 migrated scenes is the intended bug fix, not drift. Committed 5375fdf.
<!-- SECTION:FINAL_SUMMARY:END -->
