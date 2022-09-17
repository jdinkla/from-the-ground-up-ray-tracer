package net.dinkla.raytracer.world

import net.dinkla.raytracer.films.IFilm
import net.dinkla.raytracer.renderer.IRenderer

// TODO needed?
class Renderer {
    var renderer: IRenderer? = null
    fun render(film: IFilm) = Render.render(film, renderer!!)
}
