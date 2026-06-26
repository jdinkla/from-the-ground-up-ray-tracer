---
id: TASK-60
title: Resolve JDK version drift across build and docs
status: Done
assignee:
  - '@claude'
created_date: '2026-06-24 22:36'
updated_date: '2026-06-26 21:36'
labels:
  - tech-debt
  - documentation
  - build
dependencies: []
references:
  - docs/arc42/11_risks_and_technical_debt.md
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
- [x] #1 There is a single source of truth for the JDK version; the build (jvmToolchain) dictates it
- [x] #2 CLAUDE.md is updated to match the build's JDK version (25), or the toolchain is deliberately changed to 21 with all three sources aligned
- [x] #3 docs/arc42/11_risks_and_technical_debt.md section 11.3 is updated to match
- [x] #4 All three sources state the same JDK version
<!-- AC:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
Aligned all target-JDK references to 25 (the authoritative jvmToolchain(25), also matched by CI's JDK 25 Temurin). Updated CLAUDE.md, arc42/02 (constraint + CI rows), arc42/03 tech stack, arc42/11 §11.3 ClassGraph row, arc42/01 (intro + researchers), arc42/04 tech-choices, arc42/09 ADR-1 title/decision/summary. Preserved the four 'Java 21 Virtual Threads' references as correct feature provenance (virtual threads were finalized in JDK 21). Also corrected the adjacent stale 'Kotlin 2.2.x' to '2.3.x' in arc42/09 decision. Pure-markdown change; no .kt touched so the Gradle/detekt build is unaffected.
<!-- SECTION:NOTES:END -->
