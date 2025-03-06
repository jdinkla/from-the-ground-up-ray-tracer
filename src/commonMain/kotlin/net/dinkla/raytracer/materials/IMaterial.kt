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

    fun getLe(sr: IShade): Color
}
