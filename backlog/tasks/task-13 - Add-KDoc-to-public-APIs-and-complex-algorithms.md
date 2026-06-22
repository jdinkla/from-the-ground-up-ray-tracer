---
id: TASK-13
title: Add KDoc to public APIs and complex algorithms
status: In Progress
assignee:
  - '@claude'
created_date: '2026-06-22 09:12'
updated_date: '2026-06-22 12:59'
labels:
  - docs
dependencies: []
references:
  - docs/arc42/11_risks_and_technical_debt.md
priority: medium
ordinal: 13000
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Comment ratio is ~4% with no KDoc on public APIs, raising the learning curve. Add KDoc to public interfaces (IGeometricObject, ILens, Tracer, WorldDefinition and the DSL scopes) and to complex algorithms (Polynomials, Grid/SparseGrid/KDTree, lens math).
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Public interfaces and DSL entry points carry KDoc explaining purpose and key parameters
- [ ] #2 Complex algorithm files (Polynomials, acceleration structures, lenses) have explanatory KDoc
<!-- AC:END -->
