# 6. Runtime View

I've created the arc42 Chapter 6: Runtime View documentation at `docs/arc42/06_runtime_view.md`. The document covers:

**6.1 Main Use Case: Rendering a Scene**
- High-level rendering flow from user input through PNG output
- Per-pixel rendering pipeline showing ray generation, intersection, and shading
- Recursive ray tracing for reflective materials

**6.2 Error Handling**
- Error handling strategy table with locations and approaches
- Null material error flow (returns RED color as visual indicator)
- GUI exception handling with dialog display
- Parallel renderer synchronization error handling

**6.3 Startup Behavior**
- CLI startup sequence with argument parsing
- Lazy world discovery via ClassGraph scanning
- GUI startup sequence
- Component initialization order table

**6.4 Data Flow**
- Scene data flow from DSL to PNG output
- Ray transformation stages table
- Parallel data flow patterns (Sequential, Parallel with 16 threads, Coroutine with 1024 blocks)
- Shadow ray data flow
- Performance counter aggregation across threads
- Key data structures table

**6.5 Runtime Variants**
- Tracer selection impact (Whitted, AreaLighting, MultipleObjects)
- Renderer selection impact with parallelism characteristics
- Resolution impact on performance