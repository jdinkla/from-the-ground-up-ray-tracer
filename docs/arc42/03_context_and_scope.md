# 3. Context and Scope

This chapter delimits the ray tracer from its environment: who and what it talks to,
and across which interfaces.

## 3.1 Business Context

The system is a standalone, offline renderer. It turns a **scene description** into a
**raster image**. It has no network services and no persistent server state; every run is a
self-contained batch (CLI) or interactive (GUI) rendering job on the local machine.

```
            +-------------------+      scene id / *.scene.kts
  Scene     |                   |<------------------------------  Scene author
  author -->|                   |
            |   Ray Tracer      |      PLY mesh files
  CLI/GUI ->|   (this system)   |<------------------------------  File system (resources/)
  user      |                   |
            |                   |------------------------------>  PNG image  -> File system
            +-------------------+
```

| Partner | Role | Input to system | Output from system |
|---------|------|-----------------|--------------------|
| **CLI user** | Runs batch renders from a terminal | `--world`, `--tracer`, `--renderer`, `--resolution` flags | A PNG file written next to the working directory; render timing and counters on stdout |
| **GUI user** | Picks and renders a scene interactively | Scene selection, render trigger (Swing UI) | The rendered image shown on screen / saved |
| **Scene author** | Defines new scenes | A Kotlin `WorldDefinition` object under `src/examples/`, or an external `*.scene.kts` script | The rendered scene |
| **File system** | Supplies meshes, receives images | `.ply` mesh files (e.g. `resources/Bunny4K.ply`) | `.png` output images |

## 3.2 Technical Context

There are three entry points into the same rendering core:

| Entry point | Mechanism | Typical use |
|-------------|-----------|-------------|
| **Command line** | `MainKt` → `CommandLine` (Clikt argument parsing) → `Render.render` | Scripted/batch rendering, benchmarking |
| **Swing GUI** | `ui/swing` desktop application | Interactive scene browsing and rendering |
| **Programmatic API** | Calling `Render.render(id, output, context) { … }` directly | Tests, the scene audit, embedding |

All three converge on the same pipeline: a `Context` (tracer creator, renderer creator,
resolution) is adapted onto a `World` built by a `WorldDefinition`, which is then rendered to a
`Film` and saved as PNG (see [Chapter 6, Runtime View](06_runtime_view.md)).

### CLI options

| Option | Values | Default |
|--------|--------|---------|
| `--world` | a scene id (e.g. `YellowAndRedSphere.kt`) or the path to a `*.scene.kts` file | `YellowAndRedSphere.kt` |
| `--tracer` | `WHITTED`, `AREA`, `MULTIPLE_OBJECTS`, `PATH_TRACE`, `GLOBAL_TRACE` | per scene / Whitted |
| `--renderer` | `SEQUENTIAL`, `FORK_JOIN`, `PARALLEL`, `NAIVE_COROUTINE`, `COROUTINE`, `VIRTUAL` | — |
| `--resolution` | `480p`, `720p`, `1080p`, `1440p`, `2160p`, `4320p` | — |

### Interfaces and data formats

| Interface | Direction | Format |
|-----------|-----------|--------|
| Scene definition | in | Kotlin DSL objects (`WorldDefinition`) discovered via classpath scan, or `*.scene.kts` scripts evaluated by the embedded Kotlin scripting host |
| Mesh import | in | Stanford **PLY** (ASCII), parsed by `utilities/PlyReader` |
| Image output | out | **PNG**, written through the `films/` abstraction (Korim on the JVM) |

The platform-independent rendering core lives in `commonMain`; JVM-specific I/O, the Swing UI,
the parallel renderers, and the scene-script host live in `jvmMain` (see
[Chapter 5](05_building_block_view.md) and the source-set note in
[Chapter 2](02_architecture_constraints.md)).

## 3.3 Scope boundary

In scope: scene description, ray–object intersection, shading/light transport, anti-aliasing,
spatial acceleration, and image output. Out of scope: real-time/interactive rendering, GPU
acceleration, networked or server rendering, and animation (single still images only).
