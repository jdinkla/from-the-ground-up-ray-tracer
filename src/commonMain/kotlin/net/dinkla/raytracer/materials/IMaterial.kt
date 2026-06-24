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

    /**
     * The colour returned by the hybrid [net.dinkla.raytracer.tracers.GlobalTrace] tracer (Suffern
     * ch. 26, section 26.4, Listings 26.6–26.8). Unlike [pathShade], this computes the **direct**
     * illumination by sampling the lights at the first hit (`sr.depth == 0`, as in ch. 18 area
     * lighting) and uses path tracing only for the **indirect** bounces — far less noisy than pure
     * path tracing when light sources are small. The radiance-flow rules (Fig 26.11) prevent the
     * direct light being counted twice: e.g. an [Emissive] surface suppresses its emission on the
     * first indirect bounce (`sr.depth == 1`) because the direct sampling already accounted for it.
     *
     * The default returns [Color.BLACK]; only materials with a global-illumination response override
     * it. Direct-lighting tracers never call this, so they are unaffected.
     */
    fun globalShade(
        world: IWorld,
        sr: IShade,
    ): Color = Color.BLACK

    fun getLe(sr: IShade): Color
}
