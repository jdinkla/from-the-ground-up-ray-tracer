package net.dinkla.raytracer.renderer

import net.dinkla.raytracer.films.IFilm

interface IRenderer {
    fun render(film: IFilm)
}
