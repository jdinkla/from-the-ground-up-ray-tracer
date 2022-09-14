package net.dinkla.raytracer.materials

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.hits.Shade
import net.dinkla.raytracer.interfaces.hash
import net.dinkla.raytracer.utilities.equals
import net.dinkla.raytracer.world.IWorld

class Emissive(private val ce: Color = Color.WHITE, val ls: Double = 1.0) : IMaterial {

    private val le: Color = ce * ls

    override fun shade(world: IWorld, sr: Shade): Color = le

    override fun areaLightShade(world: IWorld, sr: Shade): Color = if ((-(sr.normal)) dot (sr.ray.direction) > 0) {
        le
    } else {
        Color.BLACK
    }

    override fun getLe(sr: Shade): Color = le

    override fun equals(other: Any?): Boolean = this.equals<Emissive>(other) { a, b ->
        a.ce == b.ce && a.ls == b.ls
    }

    override fun hashCode(): Int = hash(ce, ls)

    override fun toString() = "Emissive($ce, $ls)"
}
