---
id: TASK-59
title: Stop gating CI on a pre-release detekt alpha
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
priority: medium
ordinal: 62000
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
versions.properties pins plugin.dev.detekt=2.0.0-alpha.5. The entire check/CI quality gate depends on an alpha build of the static-analysis plugin. Rule sets, defaults, and baseline semantics can change between alphas, so a routine refreshVersions bump can silently alter what passes or break the build. This is a build-reproducibility and operational risk. See TECH_DEBT_REPORT.md section P2 #3.

Locations: versions.properties:33 (plugin.dev.detekt=2.0.0-alpha.5); build.gradle.kts:3 (id("dev.detekt")).
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 detekt is pinned to the latest stable release, OR a short documented rationale explains why the 2.0 alpha is required
- [ ] #2 If the alpha is kept, the upgrade to a stable detekt is tracked as a follow-up
- [ ] #3 ./gradlew clean check is green after the change
<!-- AC:END -->
