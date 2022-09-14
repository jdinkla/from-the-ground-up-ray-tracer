package net.dinkla.raytracer.textures

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.hits.IShade

abstract class Texture {

    abstract fun getColor(sr: IShade): Color
}
