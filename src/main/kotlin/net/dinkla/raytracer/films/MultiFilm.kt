package net.dinkla.raytracer.films

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.utilities.Resolution

import java.util.ArrayList

/**
 * TODO Geht das besser zu implementieren ?
 */
class MultiFilm : IFilm {

    protected var films: MutableList<IFilm>

    override val resolution: Resolution
        get() = films[0].resolution

    init {
        films = ArrayList()
    }

    fun add(film: IFilm) {
        films.add(film)
    }

    override fun initialize(numFrames: Int, resolution: Resolution) {
        for (film in films) {
            film.initialize(numFrames, resolution)
        }
    }

    override fun finish() {
        for (film in films) {
            film.finish()
        }

    }

    override fun setPixel(frame: Int, x: Int, y: Int, color: Color) {
        for (film in films) {
            film.setPixel(frame, x, y, color)
        }
    }

    override fun setBlock(frame: Int, x: Int, y: Int, width: Int, height: Int, color: Color) {
        for (film in films) {
            film.setBlock(frame, x, y, width, height, color)
        }
    }
}
