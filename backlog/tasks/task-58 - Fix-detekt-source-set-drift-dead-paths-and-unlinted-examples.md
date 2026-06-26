---
id: TASK-58
title: 'Fix detekt source-set drift: dead paths and unlinted examples'
status: Done
assignee:
  - '@claude'
created_date: '2026-06-24 22:36'
updated_date: '2026-06-26 20:52'
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
- [x] #1 The non-existent src/main/java and src/main/kotlin paths are removed from the detekt source configuration
- [x] #2 A decision is made and implemented on whether src/examples/kotlin is linted by detekt (recommended: yes)
- [x] #3 If examples are linted, surfaced violations are either fixed or deliberately baselined (a relaxed rule subset for scene DSLs is acceptable and documented)
- [x] #4 The two sub-steps (remove dead paths; lint examples) each leave the build green on their own, not only at the end
- [x] #5 ./gradlew detekt and ./gradlew clean check are green
<!-- AC:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
Step 1 (committed separately): removed non-existent src/main/java and src/main/kotlin from detekt source config. Step 2: added src/examples/kotlin to detekt source. That surfaced 1501 issues, all in three rules inappropriate for declarative scene DSLs: 1474 MagicNumber, 18 MaxLineLength, 9 LongMethod. Implemented the recommended relaxed subset: added '**/examples/**' to the excludes of MagicNumber, LongMethod, and MaxLineLength in detekt-config.yml, each with an inline rationale comment referencing this task. Examples are now linted for all OTHER rules (and are clean). ./gradlew detekt and clean check both green.
<!-- SECTION:NOTES:END -->
