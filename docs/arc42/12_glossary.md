# 12. Glossary

This glossary provides definitions for domain-specific, technical, and project-specific terms used throughout this ray tracer implementation.

## 12.1 Domain Terms

| Term | Definition | Context |
|------|------------|---------|
| **Ambient Light** | Global illumination component that affects all surfaces equally, regardless of position or orientation. Simulates indirect light bouncing in an environment. | Lighting model; implemented as `Ambient` class |
| **Ambient Occlusion** | Soft shadowing technique that darkens areas where surfaces are close together, simulating blocked ambient light. | Advanced shading; implemented as `AmbientOccluder` |
| **Area Light** | Light source distributed over a geometric surface (rectangle or disk), producing soft shadows with penumbra regions. | Soft shadow rendering; `AreaLight` class |
| **BRDF** | Bidirectional Reflectance Distribution Function. Describes how light reflects from a surface at different angles. | Material system; `Lambertian`, `GlossySpecular`, `PerfectSpecular` classes |
| **Depth of Field** | Optical effect where objects at certain distances appear sharp while others are blurred. Simulated using thin lens sampling. | Camera effects; `ThinLens` camera |
| **Hit** | The intersection point where a ray meets a geometric object, containing distance, normal, and material information. | Ray-object intersection; `IHit`, `Shade` classes |
| **Index of Refraction (IOR)** | Material property determining how much light bends when passing through a transparent medium. Glass ≈ 1.5, water ≈ 1.33. | Transparent materials; `kt` parameter |
| **Matte** | Non-reflective diffuse material with ambient and diffuse components. Scatters light equally in all directions. | Material type; `Matte` class |
| **Mesh** | Collection of triangles forming complex geometry, typically loaded from PLY files. | 3D model support; `Mesh`, `MeshTriangle` classes |
| **Normal** | Unit vector perpendicular to a surface at a given point. Essential for lighting calculations. | Surface geometry; `Normal` class |
| **Phong** | Material model combining ambient, diffuse (Lambertian), and specular (glossy) reflection components. | Material type; `Phong` class |
| **Radiance** | Physical quantity measuring light traveling in a specific direction from a point, returned by material shading functions. | Light transport; `L` and `Le` methods |
| **Ray** | Mathematical construct with an origin point and direction vector, used to trace light paths through a scene. | Core concept; `Ray` class |
| **Ray Casting** | Basic rendering technique of shooting rays from the camera through each pixel to determine visible surfaces. | Rendering algorithm; `RayCast` tracer |
| **Reflective** | Material that reflects light like a mirror, tracing secondary rays to capture reflected objects. | Material type; `Reflective` class |
| **Sampler** | Generates sample patterns for anti-aliasing, soft shadows, and depth of field effects. | Image quality; `Sampler` class with multiple strategies |
| **Shadow Ray** | Secondary ray cast from a surface point toward a light source to determine if the point is in shadow. | Shadow computation |
| **Smooth Triangle** | Triangle with per-vertex normals enabling smooth shading through normal interpolation (Phong shading). | Smooth surfaces; `SmoothTriangle` class |
| **Specular** | Mirror-like reflection producing highlights on shiny surfaces. Controlled by exponent parameter. | Material property; `ks`, `exp` parameters |
| **Torus** | Doughnut-shaped geometric primitive defined by major radius (ring size) and minor radius (tube thickness). | Geometric object; `Torus` class |
| **Transparent** | Material combining reflection and refraction for glass-like surfaces, with optional total internal reflection. | Material type; `Transparent` class |
| **View Plane** | Virtual image surface defining pixel resolution, sample count, and recursion depth limits. | Rendering setup; `ViewPlane` class |
| **Whitted Ray Tracing** | Classic recursive ray tracing algorithm following reflection and refraction rays to simulate optical effects. | Rendering algorithm; `Whitted` tracer |

## 12.2 Technical Terms

| Term | Definition | Context |
|------|------------|---------|
| **Acceleration Structure** | Spatial data structure that speeds up ray-object intersection tests by culling objects that cannot be hit. | Performance optimization; `Grid`, `KDTree` |
| **Axis-Aligned Bounding Box (AABB)** | Box with faces parallel to coordinate axes, used to enclose geometry for fast intersection rejection. | Acceleration; `BBox`, `AlignedBox` classes |
| **Compound** | Container aggregating multiple geometric objects into a single logical unit with shared transformations. | Scene organization; `Compound` class |
| **Coroutine** | Kotlin lightweight concurrent primitive used for parallel rendering with structured concurrency. | Parallelization; `CoroutineBlockRenderer` |
| **DSL** | Domain-Specific Language. Kotlin-based fluent API for defining scenes using declarative syntax. | Scene definition; `WorldScope`, builder functions |
| **Fork/Join** | Java parallel programming framework using work-stealing for divide-and-conquer algorithms. | Parallelization; `ForkJoinRenderer` |
| **Grid** | 3D spatial subdivision structure dividing space into uniform cells for acceleration. | Acceleration; `Grid`, `SparseGrid`, `TunableGrid` |
| **Instance** | Transformed reference to a geometric object, applying rotation, scaling, or translation without duplicating geometry. | Scene composition; `Instance` class |
| **KD-Tree** | Binary space partitioning tree that recursively divides space along axis-aligned planes. | Acceleration; `KDTree` class |
| **PLY Format** | Polygon File Format for storing 3D mesh data including vertices and face connectivity. | 3D model import; `Bunny4K.ply`, `Isis.ply` |
| **Renderer** | Component responsible for iterating over pixels and coordinating parallel execution of ray tracing. | Architecture; `IRenderer` interface |
| **Tracer** | Component implementing the ray tracing algorithm, determining how rays interact with the scene. | Architecture; `Tracer` interface |
| **UVW Basis** | Local orthonormal coordinate system for cameras, where U is right, V is up, and W is the viewing direction. | Camera orientation; `Basis` class |
| **Virtual Threads** | Java 19+ lightweight threads enabling high-concurrency rendering with minimal overhead. | Parallelization; `VirtualThreadBlockRenderer` |
| **World** | Complete scene container holding camera, lights, materials, geometric objects, and rendering settings. | Scene representation; `World` class |

## 12.3 Abbreviations and Conventions

| Term | Definition | Context |
|------|------------|---------|
| **cd, ce, cr, cs** | Color values: diffuse, emissive, reflective, specular. | Material parameters |
| **d** | Distance or direction parameter, often focal distance in camera lenses. | Camera, ray parameters |
| **exp** | Specular exponent controlling highlight sharpness. Higher values create tighter highlights. | Phong materials |
| **ka, kd, ks** | Material coefficients: ambient, diffuse, specular reflection amounts (0.0 to 1.0). | Material parameters |
| **kr, kt** | Reflection and transmission coefficients for transparent materials. | Transparent materials |
| **ls** | Light scale/intensity multiplier. | Light sources |
| **pdf** | Probability Density Function value used in importance sampling calculations. | Sampling |
| **sr** | Shading record containing hit information and material data. | Hit processing |
| **t** | Ray parameter representing distance from origin along the ray direction. | Ray intersection |
| **wi, wo, wt** | Direction vectors: incoming (toward light), outgoing (toward camera), transmitted (refracted). | BRDF/BTDF calculations |
| **720p, 1080p, 2160p** | Standard video resolutions: 1280×720, 1920×1080, 3840×2160 pixels. | Rendering resolution |

## 12.4 Renderer Types

| Term | Definition | Context |
|------|------------|---------|
| **SEQUENTIAL** | Single-threaded renderer processing pixels line-by-line. Simplest but slowest approach. | `SequentialRenderer` |
| **FORK_JOIN** | Parallel renderer using Java's ForkJoinPool for work-stealing parallelism. | `ForkJoinRenderer` |
| **PARALLEL** | Renderer using Java Parallel Streams for implicit parallelization. | `ParallelRenderer` |
| **COROUTINE** | Kotlin coroutine-based renderer with structured concurrency and cancellation support. | `CoroutineBlockRenderer` |
| **VIRTUAL** | Renderer using Java 19+ virtual threads for lightweight parallelism. | `VirtualThreadBlockRenderer` |

## 12.5 Tracer Types

| Term | Definition | Context |
|------|------------|---------|
| **RayCast** | Simple non-recursive tracer returning direct illumination only. | Basic rendering |
| **Whitted** | Recursive tracer following reflection and refraction rays up to maximum depth. | Full ray tracing |
| **AreaLighting** | Tracer using area light sampling for soft shadow computation. | Soft shadows |
| **MultipleObjects** | Basic tracer supporting scenes with multiple geometric objects. | Simple scenes |

## 12.6 Design Patterns

| Term | Definition | Context |
|------|------------|---------|
| **Builder Pattern** | Fluent API pattern used in the DSL for constructing complex scene objects step-by-step. | `WorldScope`, `*Scope` classes |
| **Composite Pattern** | Tree structure where individual objects and compositions are treated uniformly. | `Compound`, `GeometricObject` |
| **Strategy Pattern** | Family of interchangeable algorithms encapsulated behind a common interface. | Tracers, Renderers, Samplers |
| **Template Method** | Abstract algorithm with customizable steps implemented by subclasses. | `Tracer.trace()` method |
