---
id: TASK-4
title: Replace unsafe non-null assertion (bang-bang) operators
status: In Progress
assignee:
  - '@claude'
created_date: '2026-06-22 09:11'
updated_date: '2026-06-22 10:57'
labels:
  - reliability
  - refactor
dependencies: []
references:
  - docs/arc42/11_risks_and_technical_debt.md
priority: high
ordinal: 4000
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
There are 70+ uses of the non-null assertion operator across production code (notably AreaLight.kt, materials Phong.kt/Matte.kt, and KDTree builders). Each is a potential mid-render NullPointerException. Replace with safe calls, requireNotNull with a message, or enforce non-null invariants by construction.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 No non-null assertion operators remain in production code, or each surviving one has a documented enforced invariant
- [ ] #2 NPE-prone paths use requireNotNull with descriptive messages where a null is genuinely illegal
- [ ] #3 detekt and tests pass
<!-- AC:END -->
