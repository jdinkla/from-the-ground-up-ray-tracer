package net.dinkla.raytracer.films

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.utilities.Resolution

class PngFilm : IFilm {

    protected var film: BufferedImageFilm

    override val resolution: Resolution
        get() = this.film.resolution

    constructor(film: BufferedImageFilm) {
        this.film = film
    }

    override fun setPixel(frame: Int, x: Int, y: Int, color: Color) {
        film.setPixel(frame, x, y, color)
    }

    override fun setBlock(frame: Int, x: Int, y: Int, width: Int, height: Int, color: Color) {
        film.setBlock(frame, x, y, width, height, color)
    }

    override fun saveAsPng(fileName: String) {
        film.saveAsPng(fileName)
    }
}
