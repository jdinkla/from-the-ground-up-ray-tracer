package net.dinkla.raytracer.tracers

import net.dinkla.raytracer.world.TracerFactory

enum class Tracers(val create: TracerFactory) {
    WHITTED( { w -> Whitted(w) }),
    AREA({ w -> AreaLighting(w) });
}