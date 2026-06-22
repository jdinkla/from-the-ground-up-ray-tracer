---
id: TASK-12
title: Make Grid mutable companion state immutable or injected
status: To Do
assignee: []
created_date: '2026-06-22 09:12'
labels:
  - concurrency
  - reliability
dependencies: []
references:
  - docs/arc42/11_risks_and_technical_debt.md
priority: medium
ordinal: 12000
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Grid.kt (companion ~410-414) exposes mutable companion-object properties (logInterval, factorSize, maxDepth) that can race during parallel rendering. Make them immutable constants or pass them as construction parameters so concurrent renders cannot interfere.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Grid tuning parameters are immutable or passed per-instance, not mutable global state
- [ ] #2 Parallel renderers cannot mutate shared Grid configuration
<!-- AC:END -->
