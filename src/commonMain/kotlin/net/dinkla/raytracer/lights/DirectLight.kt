package net.dinkla.raytracer.lights

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.hits.IShade
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.math.Vector3D
import net.dinkla.raytracer.world.IWorld

/**
 * A "direct" light that shades a point through a single incoming direction: point, directional,
 * ambient, and environment lights. These are the lights the per-light loop in the direct-lighting
 * materials (`Matte`, `Phong`, and their spatially-varying variants) iterates.
 *
 * Split from the base [Light] (TASK-63) so that [AreaLight] — which is sampled over an emitter by the
 * `AreaLighting` tracer rather than evaluated through a single direction — need not stub [l],
 * [getDirection], and [inShadow] with UnsupportedOperationException. An area light is a [Light] but
 * not a [DirectLight].
 */
interface DirectLight : Light {
    fun l(
        world: IWorld,
        sr: IShade,
    ): Color

    fun getDirection(sr: IShade): Vector3D

    fun inShadow(
        world: IWorld,
        ray: Ray,
        sr: IShade,
    ): Boolean
}
