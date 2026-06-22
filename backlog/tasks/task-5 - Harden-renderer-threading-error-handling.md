---
id: TASK-5
title: Harden renderer threading error handling
status: In Progress
assignee:
  - '@claude'
created_date: '2026-06-22 09:11'
updated_date: '2026-06-22 11:10'
labels:
  - reliability
  - concurrency
dependencies: []
references:
  - docs/arc42/11_risks_and_technical_debt.md
priority: high
ordinal: 5000
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
ParallelRenderer.kt catches InterruptedException and BrokenBarrierException but only prints a stack trace, so parallel-render failures fail silently. It also throws a generic RuntimeException with a cryptic message, and Polynomials.kt throws a bare AssertionError. Propagate or recover from threading failures and use specific exception types with descriptive messages across the renderer strategies.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Threading exceptions are propagated or recovered, never silently swallowed
- [ ] #2 Render failures surface a clear, contextual error (no bare printStackTrace)
- [ ] #3 Generic RuntimeException/AssertionError replaced with specific typed exceptions carrying messages
<!-- AC:END -->
