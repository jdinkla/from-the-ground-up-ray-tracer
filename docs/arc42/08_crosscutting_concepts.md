# 8. Crosscutting Concepts

I have generated the arc42 Chapter 8: Crosscutting Concepts documentation at `docs/arc42/08_crosscutting_concepts.md`.

The document covers the following crosscutting concerns found in the codebase:

**8.1 Domain Model** - Documents the rich entity hierarchy including mathematical primitives (Ray, Point3D, Vector3D), Color model with arithmetic operations, material system (Matte, Phong, Reflective, Transparent), and the World aggregate root.

**8.2 Domain-Specific Language (DSL)** - Details the type-safe Kotlin DSL for scene description using nested scopes and lambda receivers, with concrete examples from the codebase.

**8.3 Concurrency and Parallelism** - Covers the five parallelization strategies (Sequential, Parallel, Fork-Join, Coroutine, Virtual Threads) with code examples showing the block partitioning approach.

**8.4 Configuration Management** - Documents the multi-layer configuration using Clikt for CLI parsing, predefined resolutions, and dynamic world discovery via ClassGraph.

**8.5 Logging** - Describes the custom lightweight Logger singleton with four log levels and consistent usage patterns.

**8.6 Error Handling** - Explains the minimal fail-fast approach with RuntimeException for configuration errors.

**8.7 Testing Approach** - Details the Kotest StringSpec style tests, test doubles pattern, and JaCoCo coverage configuration with strategic exclusions.

**8.8 Code Quality** - Covers Detekt static analysis and Kotlin idioms (immutable data classes, operator overloading, extension functions).

**8.9 Dependencies** - Lists core and test dependencies with their purposes.