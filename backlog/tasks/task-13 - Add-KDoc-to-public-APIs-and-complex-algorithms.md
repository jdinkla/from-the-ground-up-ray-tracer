---
id: TASK-13
title: Add KDoc to public APIs and complex algorithms
status: In Progress
assignee:
  - '@claude'
created_date: '2026-06-22 09:12'
updated_date: '2026-06-22 13:00'
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

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Read all in-scope files to document CURRENT behavior accurately.
2. Add KDoc to public interfaces: IGeometricObject (type-level + undocumented members only; TASK-10 method KDoc already present), ILens, Tracer, WorldDefinition.
3. Add KDoc to DSL scopes (WorldScope, LightsScope, MaterialsScope, ObjectsScope, InstanceScope, MetadataScope) + key entry points (camera, ambientLight, lights, materials, objects, metadata, light/material/shape declarers).
4. Add KDoc to lens math: ILens, AbstractLens, Pinhole, ThinLens, FishEye, Spherical.
5. Add KDoc to Polynomials.kt public solvers (solveQuadric/solveCubic/solveQuartic) - helpers already documented.
6. Add KDoc to acceleration: Grid/SparseGrid already class+hook documented (TASK-10); add type/method KDoc to KDTree, Node, InnerNode, Leaf, TreeBuilder + builders. GridTraversal already documented.
7. Comments-only diff. Run 'just test' (incl detekt, MaxLineLength=120). Confirm green.
<!-- SECTION:PLAN:END -->
