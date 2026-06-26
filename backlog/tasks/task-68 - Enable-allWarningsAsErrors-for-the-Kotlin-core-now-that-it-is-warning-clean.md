---
id: TASK-68
title: Enable allWarningsAsErrors for the Kotlin core now that it is warning-clean
status: Done
assignee:
  - '@claude'
created_date: '2026-06-26 20:56'
updated_date: '2026-06-26 21:39'
labels:
  - tech-debt
  - build
dependencies: []
priority: low
ordinal: 73000
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
TASK-61 removed the last compiler warnings (the unchecked casts in PlyReader and GridStructuresTest); compileKotlin/compileTestKotlin now emit zero w: lines. With the core warning-clean, the build can promote warnings to errors so new warnings fail the gate instead of accumulating invisibly. Add kotlin compiler option allWarningsAsErrors = true (compilerOptions { allWarningsAsErrors.set(true) } in build.gradle.kts), run ./gradlew clean check, and address anything it surfaces across all source sets (commonMain/jvmMain/commonTest/jvmTest/examples). If examples produce unavoidable warnings, scope the flag to the core source sets. Follow-up from TASK-61 AC#6.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 allWarningsAsErrors is enabled for the Kotlin core (commonMain/jvmMain at minimum)
- [x] #2 ./gradlew clean check is green with the flag on
<!-- AC:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
Enabled compilerOptions { allWarningsAsErrors.set(true) } in the kotlin{} block of build.gradle.kts, applying to all source sets (commonMain/jvmMain/examples + tests). clean check green with the flag on — the core is warning-clean (TASK-61 removed the last unchecked-cast warnings). The residual 'WARNING: sun.misc.Unsafe' lines are JVM-runtime notices from the Kotlin compiler daemon, not Kotlin compile warnings, so they are unaffected. A new compiler warning will now fail the build.
<!-- SECTION:NOTES:END -->
