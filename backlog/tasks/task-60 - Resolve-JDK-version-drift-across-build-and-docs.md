---
id: TASK-60
title: Resolve JDK version drift across build and docs
status: To Do
assignee: []
created_date: '2026-06-24 22:36'
labels:
  - tech-debt
  - documentation
  - build
dependencies: []
references:
  - TECH_DEBT_REPORT.md
priority: medium
ordinal: 63000
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
The target JDK version disagrees across three sources. build.gradle.kts sets jvmToolchain(25), but CLAUDE.md says 'JVM-only, JDK 21' and docs/arc42/11 section 11.3 references 'compatibility with Java 21'. Two numbers, three sources; the build (25) is authoritative. Contributors provision the wrong JDK and CI/docs disagreement causes 'works on my machine' friction. See TECH_DEBT_REPORT.md section P2 #4.

Locations: build.gradle.kts:35 (jvmToolchain(25)); CLAUDE.md (~line 8); docs/arc42/11_risks_and_technical_debt.md:39.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 There is a single source of truth for the JDK version; the build (jvmToolchain) dictates it
- [ ] #2 CLAUDE.md is updated to match the build's JDK version (25), or the toolchain is deliberately changed to 21 with all three sources aligned
- [ ] #3 docs/arc42/11_risks_and_technical_debt.md section 11.3 is updated to match
- [ ] #4 All three sources state the same JDK version
<!-- AC:END -->
