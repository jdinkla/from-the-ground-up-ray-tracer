package net.dinkla.raytracer.cameras.render

import net.dinkla.raytracer.films.IFilm

interface IRenderer {

    fun render(film: IFilm)

}
