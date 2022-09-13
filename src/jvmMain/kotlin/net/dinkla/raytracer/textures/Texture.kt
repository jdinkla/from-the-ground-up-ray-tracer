package net.dinkla.raytracer.textures

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.hits.Shade

abstract class Texture {

    abstract fun getColor(sr: Shade): Color
}
