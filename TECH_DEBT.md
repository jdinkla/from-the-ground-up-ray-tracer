# Code Coverage Gaps
- `src/commonMain/kotlin/net/dinkla/raytracer/utilities/GridUtilities.kt`: Tessellation helpers untested; add cases for flat/smooth spheres with varying step counts to cover loops, pole triangles, and normals.
- `src/commonMain/kotlin/net/dinkla/raytracer/objects/acceleration/Grid.kt`: Added smoke tests for initialization and traversal; still need coverage for already-initialized/empty fast paths, cell insertion branches (Grid vs Compound vs Null), failure path when cell division is invalid, and `hit` traversal for bbox reject, `t0>t1`, and positive/negative direction stepping.
- `src/commonMain/kotlin/net/dinkla/raytracer/objects/acceleration/SparseGrid.kt`: Added basic insertion/traversal test; still need full coverage mirroring Grid plus failure cases.
- `src/commonMain/kotlin/net/dinkla/raytracer/objects/SmoothTriangle.kt`: Branch tests added; consider adding more varied normal interpolation cases if needed.
- `src/commonMain/kotlin/net/dinkla/raytracer/objects/Torus.kt`: Branch tests added; consider additional cases for edge epsilon handling if behavior changes.
- `src/jvmMain/kotlin/net/dinkla/raytracer/renderer/*.kt`: ParallelRenderer now has success/failure tests; still need coverage for ForkJoinRenderer, CoroutineBlockRenderer, NaiveCoroutineRenderer, and VirtualThreadBlockRenderer threading/barrier paths.

Coverage snapshot (JaCoCo after latest run):
- Overall: ~51% instructions, 38% branches (`build/reports/jacoco/test/html/index.html`).
- Top uncovered files by missed instructions/branches:
  - `net.dinkla.raytracer.utilities/GridUtilities.kt` – 1282 / 16
  - `.../objects/acceleration/kdtree/builder/ObjectMedian2Builder.kt` – 754 / 34
  - `.../objects/acceleration/kdtree/builder/Test2Builder.kt` – 697 / 54
  - `.../objects/acceleration/kdtree/builder/TestBuilder.kt` – 547 / 40
  - `.../objects/acceleration/kdtree/builder/Simple2Builder.kt` – 544 / 36
  - `.../objects/acceleration/kdtree/builder/ObjectMedianBuilder.kt` – 495 / 42
  - `objects/Triangle.kt` – 471 / 16
  - `objects/OpenCylinder.kt` – 413 / 36
  - `objects/acceleration/Grid.kt` – 388 / 57
  - `objects/acceleration/SparseGrid.kt` – 332 / 58
