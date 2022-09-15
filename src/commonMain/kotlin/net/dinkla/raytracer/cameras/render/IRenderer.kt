package net.dinkla.raytracer.cameras.render

import net.dinkla.raytracer.films.Film


interface IRenderer {

    fun render(film: Film)

}
