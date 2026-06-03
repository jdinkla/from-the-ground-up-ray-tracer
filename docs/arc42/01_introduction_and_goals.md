# 1. Introduction and Goals

Now I have enough information to write the arc42 Chapter 1 documentation.

# 1. Introduction and Goals

## 1.1 Requirements Overview

### Purpose

The **From the Ground Up Ray Tracer** is a photorealistic 3D rendering engine implemented in Kotlin. It generates high-quality images by simulating the physical behavior of light as it interacts with virtual objects, materials, and light sources. The project originated as a learning exercise based on Kevin Suffern's book "Ray Tracing from the Ground Up" (2010) and has evolved through multiple technology iterations (Java → Groovy → Kotlin) into a modern, idiomatic Kotlin implementation targeting JVM 21.

### Problem Domain

Ray tracing is a rendering technique that simulates the path of light rays from a virtual camera through a scene, calculating intersections with geometric objects and computing colors based on material properties, lighting conditions, and optical phenomena such as reflection and refraction. The system addresses the computational challenge of producing physically plausible images while maintaining reasonable rendering times through parallelization and spatial acceleration structures.

### Key Functional Requirements

| Requirement | Description |
|-------------|-------------|
| **Scene Definition via DSL** | Provide a type-safe Kotlin DSL for declarative scene description, allowing users to define cameras, lights, materials, and geometric objects without imperative code (`Builder.build { ... }`) |
| **Multiple Ray Tracing Algorithms** | Support different tracing strategies: Whitted-style recursive ray tracing for reflections, area lighting with Monte Carlo integration for soft shadows |
| **Material System** | Implement physically-based materials including matte (Lambertian), Phong (specular highlights), reflective (mirrors), transparent (glass with refraction), and emissive surfaces |
| **Geometric Primitives** | Support diverse geometric objects: spheres, planes, triangles (flat and smooth-shaded), cylinders, tori, boxes, disks, and imported PLY mesh files |
| **Lighting Models** | Provide point lights, directional lights, area lights for soft shadows, ambient light, and ambient occlusion |
| **Camera Models** | Support multiple lens types: pinhole (standard perspective), thin lens (depth of field), fisheye, and spherical panoramic projection |
| **Parallel Rendering** | Offer six parallelization strategies: sequential, ForkJoinPool, Java parallel streams, coroutines (naive and block-based), and Java 21 virtual threads |
| **Spatial Acceleration** | Implement acceleration structures (Grid, SparseGrid, KDTree with multiple build strategies) to efficiently handle complex scenes with thousands of objects |
| **Anti-Aliasing** | Provide nine sampling patterns (regular, jittered, multi-jittered, N-rooks, Hammersley, etc.) for anti-aliasing and Monte Carlo integration |
| **Multiple Output Resolutions** | Support standard resolutions from 480p to 4320p (8K) |
| **User Interfaces** | Provide both command-line interface for batch rendering and Swing GUI for interactive scene selection and rendering |

### Scope

The system is designed as an educational and experimental ray tracer that demonstrates core ray tracing concepts with production-quality code organization. It includes 56 example scenes demonstrating various rendering techniques, from basic sphere rendering to complex scenes with transparent materials, area lighting, and acceleration structures.

---

## 1.2 Quality Goals

| Priority | Quality Goal | Description |
|----------|--------------|-------------|
| 1 | **Performance Efficiency** | Rendering is computationally intensive, requiring millions of ray-object intersection tests per image. The architecture prioritizes time efficiency through six parallel rendering strategies (`SEQUENTIAL`, `FORK_JOIN`, `PARALLEL`, `NAIVE_COROUTINE`, `COROUTINE`, `VIRTUAL`) and spatial acceleration structures (Grid, KDTree with 7 builder strategies). The 8GB JVM heap allocation reflects the memory requirements for large mesh scenes. |
| 2 | **Modifiability** | The codebase must remain extensible for adding new geometric objects, materials, light types, and rendering algorithms. This is achieved through strategy patterns (e.g., `Tracer` interface with multiple implementations), composition over inheritance (materials compose BRDF/BTDF components), and the DSL architecture that separates scene description from rendering logic. |
| 3 | **Functional Correctness** | Ray tracing requires mathematically precise implementations of geometric intersections, BRDF calculations, and light transport equations. The system implements well-established algorithms from Suffern's book with careful attention to numerical stability (e.g., `K_EPSILON` constants for intersection tolerances). |
| 4 | **Usability** | The Kotlin DSL provides a declarative, type-safe interface for scene creation that reads like a natural description of the scene rather than imperative construction code. Helper functions (`p()`, `v()`, `n()`, `c()`) reduce verbosity for common mathematical types. |
| 5 | **Maintainability** | Enforced through Detekt static analysis (complexity limits, naming conventions, line length restrictions), official Kotlin code style, and JaCoCo test coverage tracking. The package structure mirrors ray tracing domain concepts (`objects/`, `materials/`, `lights/`, `cameras/`, `tracers/`). |

---

## 1.3 Stakeholders

| Role | Description | Expectations |
|------|-------------|--------------|
| **Developer/Maintainer** | Jörn Dinkla, the author who has maintained this project since 2010 through multiple technology transitions (Java → Groovy → Kotlin) | Clean, well-organized code following Kotlin idioms; ability to experiment with new rendering techniques and parallelization strategies; educational value for understanding ray tracing algorithms |
| **Learners/Students** | Developers studying ray tracing concepts or Kotlin programming patterns | Clear mapping between code and ray tracing theory from Suffern's book; readable DSL examples demonstrating various rendering techniques; well-documented 56 example scenes organized by feature (materials, lighting, acceleration) |
| **Contributors** | Potential open-source contributors (project is Apache 2.0 licensed on GitHub) | Consistent code style enforced by Detekt; CI pipeline with automated builds and test reports; clear package organization following ray tracing domain concepts |
| **Kotlin Enthusiasts** | Developers interested in Kotlin language features (DSLs, coroutines, operator overloading) | Idiomatic Kotlin patterns: type-safe builder DSL using scope receivers, operator overloading for vector/point arithmetic (`+`, `-`, `*`, `dot`), coroutine-based rendering, data classes for value types |
| **Performance Researchers** | Those benchmarking different parallelization strategies on JVM 21 | Six rendering strategies for comparison (sequential baseline through virtual threads); configurable resolution scaling; acceleration structure options for controlled experiments |