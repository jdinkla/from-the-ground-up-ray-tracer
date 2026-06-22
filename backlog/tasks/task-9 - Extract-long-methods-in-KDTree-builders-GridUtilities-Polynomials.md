---
id: TASK-9
title: 'Extract long methods in KDTree builders, GridUtilities, Polynomials'
status: To Do
assignee: []
created_date: '2026-06-22 09:11'
labels:
  - refactor
dependencies: []
references:
  - docs/arc42/11_risks_and_technical_debt.md
priority: medium
ordinal: 9000
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Several methods exceed the 60-line threshold: Simple2Builder.kt (129), ObjectMedianBuilder.kt (103), GridUtilities.kt (84 and 96), Polynomials.kt (74). Extract cohesive blocks into well-named private helpers to improve readability and testability. Pairs naturally with the coverage tasks.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 No method in the listed files exceeds the 60-line threshold
- [ ] #2 Behavior unchanged; existing and new tests pass
<!-- AC:END -->
