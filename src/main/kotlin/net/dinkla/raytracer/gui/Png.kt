package net.dinkla.raytracer.gui

import net.dinkla.raytracer.films.BufferedImageFilm
import net.dinkla.raytracer.world.WorldDef

object Png {

    fun renderAndSave(wdef: WorldDef, pngFileName: String) {
        val w = wdef.world()
        w.initialize()
        val film = BufferedImageFilm(w.viewPlane.resolution)
        w.render(film)
        film.saveAsPng(pngFileName)
    }

}