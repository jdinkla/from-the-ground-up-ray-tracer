package net.dinkla.raytracer.films

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.utilities.Resolution

actual class Film {
    actual val resolution: Resolution
        get() = TODO("Not yet implemented")

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
    }
}