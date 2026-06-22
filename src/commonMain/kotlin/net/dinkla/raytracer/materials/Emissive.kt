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

    override fun getLe(sr: IShade): Color = le

    override fun equals(other: Any?): Boolean =
        this.equals<Emissive>(other) { a, b ->
            a.ce == b.ce && a.ls == b.ls
        }

    override fun hashCode(): Int = Objects.hash(ce, ls)

    override fun toString() = "Emissive($ce, $ls)"
}
