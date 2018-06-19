package net.dinkla.raytracer.films

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.utilities.Resolution

import java.util.ArrayList

class MultiFilm : IFilm {

    protected var films: MutableList<IFilm>

    override val resolution: Resolution
        get() = films[0].resolution

    constructor(numFrames: Int, resolution: Resolution) {
        films = ArrayList()
    }

    fun add(film: IFilm) {
        films.add(film)
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

    override fun saveAsPng(fileName: String) {
        for (film in films) {
            film.saveAsPng(fileName)
        }
    }
}
