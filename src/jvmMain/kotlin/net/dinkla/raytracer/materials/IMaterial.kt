package net.dinkla.raytracer.materials

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.hits.Shade
import net.dinkla.raytracer.world.IWorld

interface IMaterial {

    fun shade(world: IWorld, sr: Shade): Color

    fun areaLightShade(world: IWorld, sr: Shade): Color

    fun getLe(sr: Shade): Color

    //abstract public RGBColor pathShade(Shade sr);

}