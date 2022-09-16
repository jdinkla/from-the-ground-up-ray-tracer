package net.dinkla.raytracer.films

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.utilities.Logger
import net.dinkla.raytracer.utilities.Resolution

actual class Film {

    actual var resolution: Resolution = Resolution.RESOLUTION_720

    actual fun setPixel(x: Int, y: Int, color: Color) {

    }

    actual fun setBlock(
        x: Int,
        y: Int,
        width: Int,
        height: Int,
        color: Color
    ) {
    }

    actual fun save(filename: String) {
        Logger.info("save to $filename")
    }

}