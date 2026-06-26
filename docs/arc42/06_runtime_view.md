# 6. Runtime View

This chapter shows how the building blocks of [Chapter 5](05_building_block_view.md)
collaborate at run time.

## 6.1 Main scenario: rendering a scene

```
User/CLI -> Main/CommandLine : parse --world/--tracer/--renderer/--resolution
CommandLine -> Render        : render(id, output, Context)
Render -> Worlds             : look up WorldDefinition by id (classpath scan)
Render -> WorldDefinition    : world()                 # build World via the DSL
Render -> Context            : adapt(world)            # wire tracer + single-ray renderer + view-plane corrector
Render -> World              : initialize()            # build acceleration structures, lights, samplers
Render -> IRenderer          : render(film)            # per-pixel ray work
IRenderer -> SingleRayRenderer: render(x, y)           # for each pixel
SingleRayRenderer -> Lens    : primary ray(s) for the pixel
SingleRayRenderer -> Tracer  : trace(ray, depth) -> Color
Render -> Film               : save(output.png)
CommandLine -> stdout        : duration + counters
```

A pixel's colour is the average over the **sampler's** sample points: for each sample the
**lens** produces a primary ray, the **tracer** computes its colour, and the **view plane**
applies gamma/tone correction before the value is written to the **film**.

## 6.2 Startup and scene discovery

At startup `Worlds.kt` uses **ClassGraph** to scan the `examples` package and build a
`worldMap` keyed by each `WorldDefinition.id`. Scenes therefore register themselves — there is
no list to maintain. `--world` may instead name an external `*.scene.kts` file, which the
`world/scripting` host compiles and evaluates through the embedded Kotlin scripting engine and
resolves to a `World` the same way.

## 6.3 Recursive ray tracing and shadows

The Whitted tracer is recursive: a reflective or transparent hit spawns secondary rays whose
colours are combined with the surface shading. Recursion is bounded by a maximum depth
(`world.shouldStopRecursion(depth)`) so reflective scenes terminate. Shadows are resolved with
**shadow rays** cast from the hit point toward each light; `World.inShadow` tests them against
the same acceleration structure used for primary rays. Area lights replace the single shadow
ray with several samples over the emitter to produce soft shadows.

## 6.4 Parallel rendering

All renderers drive the *same* single-ray renderer; they differ only in how pixel work is
distributed:

| Renderer | Strategy |
|----------|----------|
| `SEQUENTIAL` | One thread, row by row (the baseline / reference image) |
| `FORK_JOIN` | `ForkJoinPool`, recursively split pixel blocks |
| `PARALLEL` | Java parallel streams over pixel rows/blocks |
| `NAIVE_COROUTINE` | One coroutine per unit of work |
| `COROUTINE` | Block-partitioned coroutines (bounded parallelism) |
| `VIRTUAL` | One virtual thread per block (Project Loom) |

The block-based renderers partition the image into rectangular **blocks** and render them
concurrently; per-thread performance counters are aggregated at the end. Because each pixel is
computed independently from immutable scene state, the strategies are expected to produce the
same image — only the wall-clock time differs.

## 6.5 Error handling at run time

- A hit with a missing material returns a sentinel colour rather than crashing the frame.
- Configuration errors (unknown resolution id, unknown world) **fail fast** with a clear
  message naming the bad value and the valid options.
- A PLY header that declares more vertices/faces than a sanity bound raises a typed
  `PlyLimitExceededException` instead of driving an `OutOfMemoryError`.

## 6.6 Runtime variants

The same scene can be rendered under different **tracers** (e.g. `WHITTED` vs `AREA` for soft
shadows), different **renderers** (Section 6.4), and different **resolutions** (`480p`–`4320p`).
Tracer and renderer are selected per run; a scene may declare a *preferred* tracer as a hint
that the CLI flag can still override.
