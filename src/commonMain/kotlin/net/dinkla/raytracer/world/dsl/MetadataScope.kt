package net.dinkla.raytracer.world.dsl

import net.dinkla.raytracer.world.Metadata

/**
 * DSL receiver for the `metadata { ... }` block, collecting the scene's descriptive fields into a
 * [Metadata] value. Fields can be set either by assignment (`id = ...`) or via the setter functions.
 */
class MetadataScope {
    var id: String = ""
    var title: String = ""
    var description: String = ""

    /** The [Metadata] built from the fields set so far. */
    val metadata: Metadata
        get() = Metadata(id, title, description)

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
}
