---
id: TASK-38
title: Scene/example coverage & health audit tool
status: In Progress
assignee:
  - '@claude'
created_date: '2026-06-23 18:29'
updated_date: '2026-06-23 18:29'
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
- [ ] #1 A Gradle task (e.g. ./gradlew audit) builds every scene from worlds() and emits a report to stdout and to a file under the repo (e.g. build/reports or docs).
- [ ] #2 Report section UNCOVERED: lists concrete production classes used by zero scenes, grouped by category (geometry, materials, lights, cameras+lenses, tracers, textures, acceleration); the full denominator per category is built by scanning production packages with classgraph, so newly added classes appear automatically.
- [ ] #3 Report section MULTIPLICITY: lists each used class with the count (and ideally ids) of scenes using it, making over-represented (e.g. Sphere) and single-example classes visible.
- [ ] #4 Report section SUSPECT RENDERS: each scene is rendered at a low resolution into a ColorGridFilm and flagged when its near-black pixel fraction exceeds a documented threshold; scenes that fail to build or render (e.g. missing .ply, exceptions) are listed in a separate FAILED group, not conflated with black images.
- [ ] #5 The pure core (object-graph walk -> used classes; coverage vs denominator; near-black fraction) is covered by frozen unit tests per the cover-first rule; the runnable/printing/Gradle glue is verified manually and that verification is reported.
- [ ] #6 The audit does NOT run as part of ./gradlew test; the full check (./gradlew build incl. detekt) stays green.
<!-- AC:END -->
