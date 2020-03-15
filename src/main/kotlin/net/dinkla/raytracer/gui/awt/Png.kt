package net.dinkla.raytracer.gui.awt

import net.dinkla.raytracer.world.WorldDef
import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
import javax.imageio.ImageIO

object Png {

    fun renderAndSave(wdef: WorldDef, pngFileName: String) {
        val w = wdef.world()
        w.initialize()
        val film = AwtFilm(w.viewPlane.resolution)
        w.renderer?.render(film)
        save(film.image, pngFileName)
    }

    fun save(img: BufferedImage, fileName: String) {
        val file = File(fileName)
        try {
            ImageIO.write(img, "png", file)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}