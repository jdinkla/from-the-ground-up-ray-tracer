package net.dinkla.raytracer.tracers

import net.dinkla.raytracer.world.World

enum class Tracers(val create: (World) -> Tracer) {
    WHITTED( { w -> Whitted(w) }),
    AREA({ w -> AreaLighting(w) });
}