# 5. Building Block View

This chapter shows the static decomposition of the system into packages and the
responsibilities of each. Package names below are relative to
`net.dinkla.raytracer`.

## 5.1 Whitebox — overall system (Level 1)

```
                         Context / Render          (world/)
                                |
            +-------------------+--------------------+
            |                   |                    |
        WorldDefinition     IRenderer            Tracer
        + Scene DSL        (renderer/)          (tracers/)
        (world/, dsl/)         |                    |
            |                  |                     |
       +----+----+            pixel work         colour of a ray
       |         |                |                    |
   materials/  objects/       Film (films/) <----- SingleRayRenderer
   brdf/btdf/  (+acceleration,    |              + cameras/ + lenses/
   lights/     mesh, compound)  PNG                + samplers/
                                              math/, hits/, colors/
```

### Level 1 building blocks

| Building block | Package | Responsibility |
|----------------|---------|----------------|
| **World & scene model** | `world/` (+ `world/dsl/`, `world/scripting/`) | `World`/`IWorld` aggregate (camera, view plane, lights, materials, objects); `Context`; `Render`; the `Builder.build { }` DSL; `*.scene.kts` host |
| **Tracers** | `tracers/` | Strategies for the colour of a ray: `Whitted`, `AreaLighting`, `MultipleObjects`, `PathTrace`, `GlobalTrace`; the `Tracers` enum |
| **Geometry** | `objects/` (+ `acceleration/`, `mesh/`, `compound/`, `beveled/`, `arealights/`) | Primitives (`Sphere`, `Plane`, `Triangle`, `Torus`, `Disk`, `OpenCylinder`, `Instance`, …), compounds, and acceleration structures (uniform/sparse grid, kd-tree) |
| **Materials** | `materials/` | `Matte`, `Phong`, `Reflective`, `Transparent`, `Emissive`, `SV_*` (textured) |
| **Reflectance / transmittance** | `brdf/`, `btdf/` | BRDF (Lambertian, GlossySpecular, PerfectSpecular, Fresnel) and BTDF components composed by materials |
| **Lights** | `lights/` | Point, directional, ambient, ambient-occluder, area lights |
| **Cameras & lenses** | `cameras/`, `cameras/lenses/` | Pinhole, ThinLens (depth of field), FishEye, Spherical projections |
| **Samplers** | `samplers/` | Anti-aliasing / Monte-Carlo patterns (Regular, Jittered, MultiJittered, NRooks, Hammersley, PureRandom, Constant) |
| **Math core** | `math/` | `Point3D`, `Vector3D`, `Normal`, `Matrix`, transforms, `BBox`, polynomial solvers |
| **Hits** | `hits/` | Intersection records (`Hit`, `Shade`, shadow records) passed down the trace |
| **Colour & film** | `colors/`, `films/`, `ViewPlane.kt` | `Color` arithmetic; the pixel raster and PNG output; gamma/tone correction |
| **Renderers** | `renderer/` (common: sequential + single-ray; jvm: parallel) | Drive per-pixel ray work; differ only in how they parallelize |
| **Texturing** | `textures/`, `noise/`, `mappings/` | Procedural and image textures, noise, and UV mappings used by spatially-varying materials |

## 5.2 Level 2 — Geometry subsystem

`objects/` separates three concerns:

- **Primitives** implement `IGeometricObject` (`hit`, `shadowHit`, `boundingBox`, `normal`).
- **Compounds** (`compound/`) hold lists of objects and intersect them as one; `Instance`
  wraps an object with an affine transform so a mesh can be reused at many positions.
- **Acceleration** (`objects/acceleration/`) wraps a compound in a spatial index: the uniform
  `Grid` and `SparseGrid` (from the book), and the `kdtree/` (from the author's diploma thesis,
  with several `TreeBuilder` split strategies). Meshes (`mesh/`) are read from PLY files into a
  compound, optionally smooth-shaded by interpolating per-vertex normals.

## 5.3 Level 2 — Rendering pipeline

`Render.render` builds the `World`, lets the `Context` wire a tracer, a single-ray renderer
(`SimpleSingleRayRenderer` = lens + tracer), and the view-plane colour corrector, calls
`world.initialize()`, then hands the `Film` to an `IRenderer`. Single-ray renderers
(`commonMain/renderer`) compute one pixel; the parallel renderers (`jvmMain/renderer`) decide
how those pixels are distributed across threads/coroutines (see
[Chapter 6](06_runtime_view.md)).

## 5.4 Source-set organisation

Although the tree uses Kotlin-Multiplatform-style source sets, this is a **JVM-only** project
(wired manually in `build.gradle.kts`):

| Source set | Contents |
|------------|----------|
| `commonMain` | The platform-independent rendering core (everything above except parallel renderers and JVM I/O) |
| `jvmMain` | JVM I/O, the Swing UI, the parallel renderers, the scene-script host, the scene audit, `Main` |
| `commonTest` / `jvmTest` | Kotest specs and shared fixtures |
| `examples` | Self-registering `WorldDefinition` scenes (compiled into `main`, coverage-excluded) |
