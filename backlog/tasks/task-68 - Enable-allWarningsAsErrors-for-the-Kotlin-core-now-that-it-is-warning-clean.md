---
id: TASK-68
title: Enable allWarningsAsErrors for the Kotlin core now that it is warning-clean
status: To Do
assignee: []
created_date: '2026-06-26 20:56'
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
- [ ] #1 allWarningsAsErrors is enabled for the Kotlin core (commonMain/jvmMain at minimum)
- [ ] #2 ./gradlew clean check is green with the flag on
<!-- AC:END -->
