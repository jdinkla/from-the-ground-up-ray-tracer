package net.dinkla.raytracer.materials

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.hits.IShade
import net.dinkla.raytracer.utilities.equals
import net.dinkla.raytracer.world.IWorld
import java.util.Objects

class Emissive(
    private val ce: Color = Color.WHITE,
    val ls: Double = 1.0,
) : IMaterial {
    private val le: Color = ce * ls

    override fun shade(
        world: IWorld,
        sr: IShade,
    ): Color = le

    override fun areaLightShade(
        world: IWorld,
        sr: IShade,
    ): Color =
        if ((-(sr.normal)) dot (sr.ray.direction) > 0) {
            le
        } else {
            Color.BLACK
        }

    /**
     * Path-tracing shade (Suffern ch. 26): an emissive surface acts as a light source, returning its
     * radiance [le] when seen from the front (the emitting side, `-(normal) . ray.direction > 0`) and
     * black otherwise — the same front-face test as [areaLightShade].
     */
    override fun pathShade(
        world: IWorld,
        sr: IShade,
    ): Color =
        if ((-(sr.normal)) dot (sr.ray.direction) > 0) {
            le
        } else {
            Color.BLACK
        }

    /**
     * Hybrid global-illumination shade (Suffern ch. 26, Listing 26.6) used by
     * [net.dinkla.raytracer.tracers.GlobalTrace]. An emitter returns its radiance [le] when seen from
     * the front (`-(normal) . ray.direction > 0`), **except** on the first indirect bounce
     * (`sr.depth == 1`), where it returns black: the diffuse surface at the first hit already sampled
     * this light directly (its [Matte.globalShade] direct term), so emitting again here would
     * double-count it (Fig 26.11). On the primary ray (`sr.depth == 0`, the camera sees the light) and
     * on deeper bounces (`sr.depth >= 2`, not covered by any direct sampling) it emits normally.
     */
    override fun globalShade(
        world: IWorld,
        sr: IShade,
    ): Color =
        if (sr.depth == 1) {
            Color.BLACK
        } else if ((-(sr.normal)) dot (sr.ray.direction) > 0) {
            le
        } else {
            Color.BLACK
        }

    override fun getLe(sr: IShade): Color = le

    override fun equals(other: Any?): Boolean =
        this.equals<Emissive>(other) { a, b ->
            a.ce == b.ce && a.ls == b.ls
        }

    override fun hashCode(): Int = Objects.hash(ce, ls)

    override fun toString() = "Emissive($ce, $ls)"
}
