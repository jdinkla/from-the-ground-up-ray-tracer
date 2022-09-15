package net.dinkla.raytracer.films

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.utilities.Png
import net.dinkla.raytracer.utilities.Resolution
import java.awt.image.BufferedImage

actual class Film {

    actual var resolution: Resolution = Resolution.RESOLUTION_1080

    val image = BufferedImage(resolution.hres, resolution.vres, BufferedImage.TYPE_INT_RGB)

    actual fun setPixel(x: Int, y: Int, color: Color) {
        assert(x >= 0)
        assert(x < resolution.hres)
        assert(y >= 0)
        assert(y < resolution.vres)
        image.setRGB(x, resolution.vres - 1 - y, color.toInt())
    }

    actual fun setBlock(x: Int, y: Int, width: Int, height: Int, color: Color) {
        var pixel: Any? = null
        pixel = image.colorModel.getDataElements(color.toInt(), pixel)
        for (j in 0 until height) {
            for (i in 0 until width) {
                image.raster.setDataElements(x + i, resolution.vres - 1 - y - j, pixel)
            }
        }
    }

    actual fun save(filename: String) {
        Png.save(image, filename)
    }

}