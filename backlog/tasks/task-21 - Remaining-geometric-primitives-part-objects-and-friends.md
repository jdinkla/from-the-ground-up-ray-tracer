---
id: TASK-21
title: Remaining geometric primitives (part objects and friends)
status: In Progress
assignee:
  - '@claude'
created_date: '2026-06-22 09:41'
updated_date: '2026-06-22 17:25'
labels:
  - enhancement
  - book-parity
dependencies: []
priority: low
ordinal: 24000
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
The book includes many primitives this port lacks: Annulus, part objects (part sphere, part cylinder, part torus, part annulus with angular/extent limits), open and solid cones, ConcaveSphere, bowl/thick-ring, and further beveled objects (beveled cylinder, beveled wedge). Long-tail parity work; each primitive is independent and can be split into its own task when picked up. Follow the cover-first rule: each new primitive needs a hit()/shadowHit() test.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Annulus plus the part-sphere / part-cylinder / part-torus primitives are implemented with correct hit and bounding box
- [ ] #2 Open and solid cones are implemented
- [ ] #3 New primitives are declarable from the Builder DSL and have hit/shadowHit unit tests
<!-- AC:END -->
