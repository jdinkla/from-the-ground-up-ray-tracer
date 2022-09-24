package net.dinkla.raytracer.world

class Metadata(
    val id: String,
    val title: String = "",
    val description: String = ""
) {
    override fun toString(): String = "World $id, $title, $description"
}
