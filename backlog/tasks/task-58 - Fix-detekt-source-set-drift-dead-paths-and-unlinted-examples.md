---
id: TASK-58
title: 'Fix detekt source-set drift: dead paths and unlinted examples'
status: To Do
assignee: []
created_date: '2026-06-24 22:36'
labels:
  - tech-debt
  - build
  - detekt
dependencies: []
references:
  - TECH_DEBT_REPORT.md
priority: high
ordinal: 61000
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
The detekt 'source' configuration in build.gradle.kts has drifted from the actual KMP-style source layout. It lists src/main/java and src/main/kotlin (neither exists), plus commonMain/jvmMain/commonTest/jvmTest. It omits src/examples/kotlin: the ~22 scene-definition files that ARE compiled into main (wired at build.gradle.kts:38) but are never statically analyzed.

Result: two configured detekt paths are dead, and the example scenes escape the quality gate. The check/CI gate silently covers less than it appears to, and the dead paths invite copy-paste drift. See TECH_DEBT_REPORT.md section P1 #2.

Locations: build.gradle.kts:98-105 (detekt source.setFrom(...)); src/examples/kotlin (present, wired into main at build.gradle.kts:38).

Land this as two independently-green steps: (1) remove the two non-existent paths; (2) decide on and implement whether to lint src/examples/kotlin. Step 2 may surface new violations (that is the point); fixing or baselining them, possibly with a relaxed rule subset for scene DSLs, is expected.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 The non-existent src/main/java and src/main/kotlin paths are removed from the detekt source configuration
- [ ] #2 A decision is made and implemented on whether src/examples/kotlin is linted by detekt (recommended: yes)
- [ ] #3 If examples are linted, surfaced violations are either fixed or deliberately baselined (a relaxed rule subset for scene DSLs is acceptable and documented)
- [ ] #4 The two sub-steps (remove dead paths; lint examples) each leave the build green on their own, not only at the end
- [ ] #5 ./gradlew detekt and ./gradlew clean check are green
<!-- AC:END -->
