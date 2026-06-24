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
    /**
     * Whether this scene is an intentionally-empty template or by-design-empty scaffolding — black by
     * design, not a defect. When `true` the audit's near-black detection excludes it from the SUSPECT
     * list, keeping that list a high-signal set of genuine problems. Defaults to `false`, so every real
     * scene is still checked; only scenes that explicitly opt out (e.g. `Template.kt`) are skipped.
     */
    val intentionallyEmpty: Boolean = false,
) {
    override fun toString(): String = "World $id, $title, $description"
}
