package net.dinkla.raytracer.materials

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.hits.IShade
import net.dinkla.raytracer.world.IWorld

interface IMaterial {
    fun shade(
        world: IWorld,
        sr: IShade,
    ): Color

    fun areaLightShade(
        world: IWorld,
        sr: IShade,
    ): Color

    /**
     * The colour returned along a path-traced ray that hit this material (Suffern ch. 26). The
     * default returns [Color.BLACK], so materials that have no global-illumination response — and the
     * existing direct-lighting tracers, which never call this — are unaffected. Diffuse materials
     * ([Matte]) override it to spawn an indirect bounce; emissive materials ([Emissive]) override it
     * to return their radiance and thereby act as light sources.
     */
    fun pathShade(
        world: IWorld,
        sr: IShade,
    ): Color = Color.BLACK

    fun getLe(sr: IShade): Color
}
