package net.dinkla.raytracer.tracers

import net.dinkla.raytracer.world.IWorld

enum class Tracers(val create: (IWorld) -> Tracer) {
    WHITTED( { w -> Whitted(w) }),
    AREA({ w -> AreaLighting(w) });
}