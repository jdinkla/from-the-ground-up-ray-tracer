---
id: TASK-67
title: Upgrade to detekt 2.0 stable once it ships
status: To Do
assignee: []
created_date: '2026-06-26 20:46'
updated_date: '2026-06-26 21:40'
labels:
  - tech-debt
  - detekt
  - build
dependencies: []
priority: low
ordinal: 72000
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
build.gradle.kts pins the detekt 2.0 line (plugin id dev.detekt) at a pre-release alpha (versions.properties: plugin.dev.detekt=2.0.0-alpha.5) because it is the only detekt line supporting Kotlin 2.3 / JDK 25 — see TASK-59 for the full rationale and the in-code comments. When detekt 2.0 reaches a stable release, bump plugin.dev.detekt to it (via ./gradlew refreshVersions or directly), remove the 'alpha is intentional' caveat language from the build.gradle.kts / versions.properties comments, and confirm ./gradlew clean check stays green.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 plugin.dev.detekt is set to a stable detekt 2.0 release (no -alpha/-beta/-RC suffix)
- [ ] #2 The 'alpha is intentional / do not pin to 1.23.x' comments are updated or removed as appropriate
- [ ] #3 ./gradlew clean check is green
<!-- AC:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
2026-06-26: ran ./gradlew refreshVersions to check — plugin.dev.detekt shows NO newer version available (still 2.0.0-alpha.5, no # available= hints), i.e. detekt 2.0 stable has not shipped yet. Task remains blocked on that external release; not actionable now. Bumping to a non-existent stable would break the build.
<!-- SECTION:NOTES:END -->
