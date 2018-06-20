package net.dinkla.raytracer.gui.swing

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.films.BufferedImageFilm
import net.dinkla.raytracer.films.IFilm
import net.dinkla.raytracer.utilities.Resolution
import java.awt.Canvas

import javax.swing.*
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent

class ImageFrame private constructor(resolution: Resolution, protected val isMainFrame: Boolean) : JFrame(), IFilm {

    val film: BufferedImageFilm = BufferedImageFilm(resolution)
    val canvas: Canvas = film.canvas

    override val resolution: Resolution
        get() = film.resolution

    constructor(resolution: Resolution) : this(resolution, false) {
        add(canvas)
        setSize(resolution.hres, resolution.vres + 22)
        addWindowListener(
                object : WindowAdapter() {
                    override fun windowClosing(ev: WindowEvent?) {
                        dispose()
                        if (isMainFrame) {
                            System.exit(0)
                        }
                    }
                })
        isVisible = true
    }

    override fun setPixel(x: Int, y: Int, color: Color) {
        film.setPixel(x, y, color)
        canvas.repaint()
    }

    override fun setBlock(x: Int, y: Int, width: Int, height: Int, color: Color) {
        film.setBlock(x, y, width, height, color)
        canvas.repaint()
    }

    override fun repaint() {
        super.repaint()
        canvas.repaint()
    }

    override fun saveAsPng(fileName: String) {
        film.saveAsPng(fileName)
    }

}
