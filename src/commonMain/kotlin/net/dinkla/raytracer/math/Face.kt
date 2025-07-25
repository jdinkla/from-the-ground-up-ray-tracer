package net.dinkla.raytracer.math

enum class Face(
    val normal: Normal,
) {
    LEFT(Normal.LEFT),
    RIGHT(Normal.RIGHT),
    FRONT(Normal.BACKWARD),
    BACK(Normal.FORWARD),
    TOP(Normal.UP),
    BOTTOM(Normal.DOWN),
}
