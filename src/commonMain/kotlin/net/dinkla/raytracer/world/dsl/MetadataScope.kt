package net.dinkla.raytracer.world.dsl

import net.dinkla.raytracer.tracers.Tracers
import net.dinkla.raytracer.world.Metadata

/**
 * DSL receiver for the `metadata { ... }` block, collecting the scene's descriptive fields into a
 * [Metadata] value. Fields can be set either by assignment (`id = ...`) or via the setter functions.
 */
class MetadataScope {
    var id: String = ""
    var title: String = ""
    var description: String = ""

    /**
     * The tracer this scene is designed for, or `null` (the default) when the scene expresses no
     * preference. Setting it makes the scene render with that tracer by default in tools like the
     * audit, while leaving the `--tracer` CLI flag free to override it.
     */
    var preferredTracer: Tracers? = null

    /** The [Metadata] built from the fields set so far. */
    val metadata: Metadata
        get() = Metadata(id, title, description, preferredTracer)

    /** Sets the scene [id]. */
    fun id(id: String) {
        this.id = id
    }

    /** Sets the scene [title]. */
    fun title(title: String) {
        this.title = title
    }

    /** Sets the scene [description]. */
    fun description(description: String) {
        this.description = description
    }

    /** Declares the [tracer] this scene is designed for (a hint, not a lock; see [preferredTracer]). */
    fun preferredTracer(tracer: Tracers) {
        this.preferredTracer = tracer
    }
}
