package net.dinkla.raytracer.textures

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.hits.IShade

class ConstantColor(private var color: Color) : Texture() {

    // Why SR?
    override fun getColor(sr: IShade): Color {
        return color
    }
}
