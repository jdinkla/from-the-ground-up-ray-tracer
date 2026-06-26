package net.dinkla.raytracer.lights

/**
 * The base role shared by every light in a [net.dinkla.raytracer.world.World]'s `lights` list: it
 * only declares whether the light casts [shadows]. The way a light actually contributes radiance is
 * left to its sub-role — [DirectLight] for single-direction lights, or [AreaLight] (sampled over an
 * emitter by the `AreaLighting` tracer). Segregated this way per TASK-63 so neither family has to
 * stub methods the other needs.
 */
interface Light {
    val shadows: Boolean
}
