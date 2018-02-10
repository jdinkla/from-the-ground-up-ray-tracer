package net.dinkla.raytracer.films

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.utilities.Resolution

import javax.imageio.ImageIO
import java.io.File
import java.io.IOException

class PngFilm : IFilm {

    protected var fileName: String
    protected var film: BufferedImageFilm

    override val resolution: Resolution
        get() = this.film.resolution

    constructor(fileName: String) {
        this.fileName = fileName
        film = BufferedImageFilm()
    }

    constructor(fileName: String, film: BufferedImageFilm) {
        this.fileName = fileName
        this.film = film
    }

    override fun initialize(numFrames: Int, resolution: Resolution) {
        film.initialize(numFrames, resolution)
    }

    override fun finish() {
        val file = File(fileName)
        try {
            ImageIO.write(film.img, "png", file)
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    override fun setPixel(frame: Int, x: Int, y: Int, color: Color) {
        film.setPixel(frame, x, y, color)
    }

    override fun setBlock(frame: Int, x: Int, y: Int, width: Int, height: Int, color: Color) {
        film.setBlock(frame, x, y, width, height, color)
    }
}
