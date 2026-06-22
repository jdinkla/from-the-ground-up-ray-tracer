---
id: TASK-15
title: Add input validation and resource limits
status: In Progress
assignee:
  - '@claude'
created_date: '2026-06-22 09:12'
updated_date: '2026-06-22 16:02'
labels:
  - reliability
dependencies: []
references:
  - docs/arc42/11_risks_and_technical_debt.md
priority: low
ordinal: 15000
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
CLI arguments are parsed without bounds checking and large PLY models (Bunny4K.ply, Isis.ply) plus grid allocation can exhaust the heap with no limits. Validate CLI inputs (resolution, world-file existence) and add configurable bounds / graceful degradation for grid allocation and large-model loading.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Invalid CLI inputs (bad resolution, missing world) fail fast with a clear message
- [ ] #2 Grid allocation and large-model loading have configurable limits and degrade gracefully instead of OOM
<!-- AC:END -->
