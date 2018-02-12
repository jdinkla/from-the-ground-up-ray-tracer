package net.dinkla.raytracer.textures

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.hits.Shade


class ConstantColor(protected var color: Color) : Texture() {

    // Why SR?
    override fun getColor(sr: Shade): Color {
        return color
    }

}
