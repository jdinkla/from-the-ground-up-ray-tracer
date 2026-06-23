---
id: TASK-38
title: Scene/example coverage & health audit tool
status: In Progress
assignee:
  - '@claude'
created_date: '2026-06-23 18:29'
updated_date: '2026-06-23 19:01'
labels:
  - tooling
  - examples
dependencies: []
ordinal: 41000
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Add a standalone 'scene audit' that builds and (low-res) renders every auto-discovered example scene to answer three questions about the example suite: (1) which production classes have NO example, (2) which classes appear in MULTIPLE examples, and (3) which examples render to a (near-)black image and are therefore likely broken. Motivation: as the ray tracer approaches the book's feature set, the example set is our living catalogue of capabilities; we currently have no way to see gaps (a primitive/material/light/camera/tracer/texture/acceleration type with no scene), redundancy (e.g. spheres in dozens of scenes vs. a type with one), or silently-broken scenes. Delivered as a Gradle task that prints + writes a report; it is NOT part of the unit-test suite (rendering ~70 scenes is too slow and some need downloaded .ply files). Scope of tracked categories is the user-facing ones authored in a scene: geometric objects, materials, lights, cameras+lenses, tracers, textures, acceleration structures (NOT internal brdf/btdf/samplers/mappings/noise). Design: a testable pure core (walk a World's object graph + materials/lights/camera/tracer to the set of concrete production classes it uses; compute coverage against a classgraph-scanned denominator of concrete classes per category; compute the near-black pixel fraction of a rendered ColorGridFilm) plus a thin runnable that iterates worlds() and emits the report. Tree walk must recurse Compound.objects, Instance.geometricObject, Grid.cells and KDTree.root, and detect Grid/KDTree as used acceleration structures.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 A Gradle task (e.g. ./gradlew audit) builds every scene from worlds() and emits a report to stdout and to a file under the repo (e.g. build/reports or docs).
- [x] #2 Report section UNCOVERED: lists concrete production classes used by zero scenes, grouped by category (geometry, materials, lights, cameras+lenses, tracers, textures, acceleration); the full denominator per category is built by scanning production packages with classgraph, so newly added classes appear automatically.
- [x] #3 Report section MULTIPLICITY: lists each used class with the count (and ideally ids) of scenes using it, making over-represented (e.g. Sphere) and single-example classes visible.
- [x] #4 Report section SUSPECT RENDERS: each scene is rendered at a low resolution into a ColorGridFilm and flagged when its near-black pixel fraction exceeds a documented threshold; scenes that fail to build or render (e.g. missing .ply, exceptions) are listed in a separate FAILED group, not conflated with black images.
- [x] #5 The pure core (object-graph walk -> used classes; coverage vs denominator; near-black fraction) is covered by frozen unit tests per the cover-first rule; the runnable/printing/Gradle glue is verified manually and that verification is reported.
- [x] #6 The audit does NOT run as part of ./gradlew test; the full check (./gradlew build incl. detekt) stays green.
<!-- AC:END -->



## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Pure, unit-tested core in jvmMain package net.dinkla.raytracer.audit:
   - Category enum (GEOMETRY, ACCELERATION, MATERIALS, LIGHTS, CAMERAS, LENSES, TEXTURES) each with a base type + classgraph scan rule; build the per-category DENOMINATOR of concrete (non-abstract, non-example) production classes. GEOMETRY excludes the acceleration package and an infra blocklist (Compound, CompoundWithMesh, NullObject); ACCELERATION = objects.acceleration concrete classes. TRACERS are intentionally NOT a category: the tracer is a render-time choice, never authored in a scene, so per-scene tracer coverage is meaningless.
   - SceneInspector.usedClasses(world): typed walk collecting runtime classes actually used: geometry tree (recurse Compound.objects; Instance child via reflection on the private field; Grid/KDTree expose authored children via Compound.objects BEFORE initialize, avoiding cell/NullObject noise), materials (world.materials values + each object.material), lights (world.lights + ambientLight), camera (+stereoCamera), lens (camera.lens), and a bounded reflective Texture collector starting from materials (depth/visited-capped, only follows net.dinkla.raytracer objects).
   - BlackImageDetector.nearBlackFraction(film,res,epsilon).
   - ReportModel + formatter: from per-scene usage build UNCOVERED (denominator - used, grouped by category), MULTIPLICITY (class -> scene ids), SUSPECT (>= threshold near-black) and FAILED (build/render threw) sections; render to markdown.
2. Thin runner: SceneAuditor (testable; takes a list of WorldDefinition + an inspect/render strategy so aggregation is unit-tested with fakes) + AuditMain main() that wires real worlds(), renders each scene at a low resolution with numSamples forced to 1 and a heuristic tracer (AreaLighting when the scene has area lights / Emissive / AmbientOccluder, else Whitted) into a ColorGridFilm, catches per-scene exceptions into FAILED, skips stereo scenes from the black check with a note, prints report to stdout and writes build/reports/scene-audit.md.
3. Gradle: register JavaExec task 'audit' -> AuditMainKt; add the audit runner entry point to the JaCoCo exclude list (like MainKt) so only the tested core counts. Do NOT wire audit into 'test'/'check'.
4. Tests (frozen, written first): SceneInspector against a hand-built World (sphere+box+grid+instance+sv-textured material) asserting the per-category used sets; denominator filtering (abstract/example/blocklist excluded); BlackImageDetector on all-black / half-black / lit films; report aggregation over fake scenes (uncovered/multiplicity/suspect/failed).
5. Manual verification (excluded glue): run ./gradlew audit, sanity-check the three sections against known scenes; confirm ./gradlew build (incl. detekt) stays green and 'test' does not run the audit.
<!-- SECTION:PLAN:END -->
