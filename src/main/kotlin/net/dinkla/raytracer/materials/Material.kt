package net.dinkla.raytracer.materials

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.hits.Shade
import net.dinkla.raytracer.worlds.World

abstract class Material {

    var shadows: Boolean = false

    init {
        shadows = true
    }

    abstract fun shade(world: World, sr: Shade): Color

    abstract fun areaLightShade(world: World, sr: Shade): Color

    //abstract public RGBColor pathShade(Shade sr);

    abstract fun getLe(sr: Shade): Color

}
