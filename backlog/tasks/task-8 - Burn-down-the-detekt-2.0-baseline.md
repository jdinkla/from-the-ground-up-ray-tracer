---
id: TASK-8
title: Burn down the detekt 2.0 baseline
status: To Do
assignee: []
created_date: '2026-06-22 09:11'
labels:
  - quality
  - tooling
dependencies: []
references:
  - docs/arc42/11_risks_and_technical_debt.md
  - detekt-baseline.xml
priority: medium
ordinal: 8000
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
The detekt 2.0 upgrade parked existing findings in detekt-baseline.xml (125 entries) so the build stays green while new issues still fail. This replaces the old build>maxIssues=150 slack. Incrementally fix the baselined findings (MagicNumber, TooGenericExceptionCaught/Thrown, PrintStackTrace, etc.) and shrink the baseline toward empty; some overlap with the refactor tasks.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 detekt-baseline.xml entry count reduced (target: trending to zero)
- [ ] #2 No new findings are added to the baseline; new issues are fixed at source
<!-- AC:END -->
