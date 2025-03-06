package net.dinkla.raytracer.tracers

import net.dinkla.raytracer.world.TracerCreator

enum class Tracers(
    val create: TracerCreator,
) {
    WHITTED({ w -> Whitted(w) }),
    AREA({ w -> AreaLighting(w) }),
    MULTIPLE_OBJECTS({ w -> MultipleObjects(w) }),
}
