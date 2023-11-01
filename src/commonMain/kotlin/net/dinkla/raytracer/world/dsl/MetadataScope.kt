package net.dinkla.raytracer.world.dsl

import net.dinkla.raytracer.world.Metadata

class MetadataScope {
    var id: String = ""
    var title: String = ""
    var description: String = ""

    val metadata: Metadata
        get() = Metadata(id, title, description)

    fun id(id: String) {
        this.id = id
    }

    fun title(title: String) {
        this.title = title
    }

    fun description(description: String) {
        this.description = description
    }
}
