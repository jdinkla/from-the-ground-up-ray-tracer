package net.dinkla.raytracer.gui.awt

import net.dinkla.raytracer.films.Film
import net.dinkla.raytracer.world.World
import net.dinkla.raytracer.world.WorldDefinition
import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
import javax.imageio.ImageIO

object Png {

    fun renderAndSave(worldDefinition: WorldDefinition, pngFileName: String) {
        val (film, world) = render(worldDefinition)
        save(film.image, pngFileName)
    }

    fun render(worldDefinition: WorldDefinition): Pair<Film, World> {
        val world = worldDefinition.init()
        val film = AwtFilm(world.viewPlane.resolution)
        world.renderer?.render(film)
        return Pair(film, world)
    }

    private fun save(img: BufferedImage, fileName: String) {
        val file = File(fileName)
        try {
            ImageIO.write(img, "png", file)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}