---
id: TASK-59
title: >-
  Document why detekt 2.0-alpha is required (Kotlin 2.3 / JDK 25) and track the
  move to detekt 2.0 stable
status: Done
assignee:
  - '@claude'
created_date: '2026-06-24 22:36'
updated_date: '2026-06-26 21:36'
labels:
  - tech-debt
  - detekt
  - documentation
dependencies: []
references:
  - docs/arc42/11_risks_and_technical_debt.md
priority: low
ordinal: 62000
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
versions.properties:33 pins plugin.dev.detekt=2.0.0-alpha.5 (plugin id dev.detekt, build.gradle.kts:3). An earlier review framed this as a build-reproducibility risk and recommended pinning to a stable detekt. That recommendation is INFEASIBLE and is corrected here.

Verified against this repo: the project is on Kotlin 2.3.0 (versions.properties: version.kotlin=2.3.0) with jvmToolchain(25). The legacy stable detekt line (1.23.x, plugin id io.gitlab.arturbosch.detekt) supports only older Kotlin/JDK combinations and does not support Kotlin 2.3 / JDK 25; the detekt 2.0 line (plugin id dev.detekt), currently pre-release, is the line that targets modern Kotlin 2.3+/JDK 25. Substituting detekt 1.23.x stable would break the build. The alpha is therefore required by the current toolchain, not gratuitous.

This is consequently a documentation/guard task, not a build risk: record why the alpha is intentional so a future refreshVersions bump or well-meaning 'pin to stable' change does not regress the build, and track the upgrade to detekt 2.0 stable once it ships. See TECH_DEBT_REPORT.md section P2 #3 (premise corrected).
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 A short comment near id("dev.detekt") in build.gradle.kts and/or near plugin.dev.detekt in versions.properties explains the detekt 2.0 alpha is intentional and required by Kotlin 2.3 / JDK 25, and that detekt 1.23.x stable must NOT be substituted
- [x] #2 A note or mechanism is in place to revisit and upgrade to detekt 2.0 once it reaches stable
- [x] #3 ./gradlew clean check stays green
<!-- AC:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
Added explanatory comments at build.gradle.kts plugins block (durable; refreshVersions never edits it) and above plugin.dev.detekt in versions.properties, stating the detekt 2.0 alpha is required for Kotlin 2.3/JDK 25 and 1.23.x stable must not be substituted. Created TASK-67 as the tracking mechanism to upgrade to detekt 2.0 stable once it ships; referenced from the comment. clean check green.
<!-- SECTION:NOTES:END -->
