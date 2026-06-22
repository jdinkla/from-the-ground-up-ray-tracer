---
id: TASK-13
title: Add KDoc to public APIs and complex algorithms
status: In Progress
assignee:
  - '@claude'
created_date: '2026-06-22 09:12'
updated_date: '2026-06-22 13:09'
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
- [x] #1 Public interfaces and DSL entry points carry KDoc explaining purpose and key parameters
- [x] #2 Complex algorithm files (Polynomials, acceleration structures, lenses) have explanatory KDoc
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

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
Documentation-only change (KDoc added; 394 insertions, 0 deletions; verified comments-only via git diff filter).

Public interfaces & DSL (AC#1):
- IGeometricObject: type-level KDoc + KDoc on previously undocumented members (isShadows, boundingBox, material, initialize, hit, both shadowHit overloads). The TASK-10 polymorphic-hook KDoc (getResultObject/combineInCell/objectCount/promotableToSubgrid/childrenForRegrid) was left untouched.
- ILens, Tracer, WorldDefinition: type + method KDoc.
- DSL scopes WorldScope/LightsScope/MaterialsScope/ObjectsScope/InstanceScope/MetadataScope: type-level KDoc + KDoc on entry points (camera, ambientLight, ambientOccluder, lights, materials, objects, metadata; the light/material/shape declarers; grid/kdtree/instance/ply nesting blocks).

Complex algorithms (AC#2):
- Polynomials.kt: object-level KDoc + KDoc on solveQuadric/solveCubic/solveQuartic (ascending-degree coeff convention, normal-form/Cardano/resolvent-cubic methods). Existing helper KDoc untouched.
- Lens math: AbstractLens, Pinhole, ThinLens (documented its STUB behaviour: ignores pixel/sample/f/d/sampler, returns fixed pm(1,1,1) ray), FishEye (unit-image-circle rejection), Spherical (panoramic mapping). Verified Basis.pm = x*u+y*v-z*w before describing directions.
- Grid.kt: added KDoc to initialize/hit/shadowHit (Amanatides-Woo 3D-DDA via GridTraversal). Class-level + hook KDoc from TASK-10 left untouched; GridTraversal.kt and SparseGrid.kt already fully documented (untouched).
- KDTree.kt, Node, InnerNode, Leaf, TreeBuilder + all builders (SpatialMedian, Simple2 [flagged the split=mid.z recorded-value quirk], ObjectMedian, ObjectMedian2, Test, Test2).

Verified: 'just test' (= ./gradlew clean check, includes detekt) BUILD SUCCESSFUL. Kept all KDoc lines <=120 (detekt MaxLineLength applies to comments). Pre-existing Unchecked-cast warnings in PlyReader.kt and GridStructuresTest.kt are unrelated (those files not touched).
<!-- SECTION:NOTES:END -->
