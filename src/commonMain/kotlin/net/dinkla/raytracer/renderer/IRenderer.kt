package net.dinkla.raytracer.renderer

import net.dinkla.raytracer.films.Film


interface IRenderer {

    fun render(film: Film)

}
