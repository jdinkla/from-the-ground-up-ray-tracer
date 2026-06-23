package net.dinkla.raytracer.world

import net.dinkla.raytracer.tracers.Tracers

class Metadata(
    val id: String,
    val title: String = "",
    val description: String = "",
    /**
     * The tracer this scene is designed for, or `null` when the scene expresses no preference (the
     * default for every historical scene). It is a *hint*, not a lock: tools that render a scene (the
     * audit) use it as their default tracer when present, but the `--tracer` CLI flag still overrides
     * it. Existing scenes that set nothing keep behaving exactly as before.
     */
    val preferredTracer: Tracers? = null,
) {
    override fun toString(): String = "World $id, $title, $description"
}
