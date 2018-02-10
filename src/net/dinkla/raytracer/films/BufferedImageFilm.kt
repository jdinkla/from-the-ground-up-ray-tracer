package net.dinkla.raytracer.films

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.utilities.Resolution

import java.awt.image.BufferedImage

class BufferedImageFilm : IFilm {

    var img: BufferedImage = BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB)

    override var resolution: Resolution = Resolution.RESOLUTION_1080

    override fun initialize(numFrames: Int, resolution: Resolution) {
        this.resolution = resolution
        img = BufferedImage(resolution.hres, resolution.vres, BufferedImage.TYPE_INT_RGB)
    }

    override fun finish() {}

    override fun setPixel(frame: Int, x: Int, y: Int, color: Color) {
        img.setRGB(x, resolution.vres - 1 - y, color.asInt())
    }

    override fun setBlock(frame: Int, x: Int, y: Int, width: Int, height: Int, color: Color) {
        var pixel: Any? = null
        pixel = img.colorModel.getDataElements(color.asInt(), pixel)
        for (j in 0 until height) {
            for (i in 0 until width) {
                img.raster.setDataElements(x + i, resolution.vres - 1 - y - j, pixel)
            }
        }
    }
}
