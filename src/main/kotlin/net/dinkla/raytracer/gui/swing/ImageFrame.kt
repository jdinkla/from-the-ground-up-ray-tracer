package net.dinkla.raytracer.gui.swing

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.films.BufferedImageFilm
import net.dinkla.raytracer.films.IFilm
import net.dinkla.raytracer.utilities.Resolution

import javax.swing.*
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent

class ImageFrame(resolution: Resolution, protected val isMainFrame: Boolean) : JFrame(), IFilm {

    val film: BufferedImageFilm
    private val canvas: ImageCanvas

    protected var counter: Int = 0

    override val resolution: Resolution
        get() = film.resolution

    init {

        film = BufferedImageFilm()
        film.initialize(1, resolution)
        canvas = ImageCanvas(film.img)
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

        counter = 0
    }

    override fun initialize(numFrames: Int, resolution: Resolution) {
        film.initialize(numFrames, resolution)
    }

    override fun finish() {
        film.finish()
    }

    override fun setPixel(frame: Int, x: Int, y: Int, color: Color) {
        film.setPixel(frame, x, y, color)
        canvas.repaint()
    }

    override fun setBlock(frame: Int, x: Int, y: Int, width: Int, height: Int, color: Color) {
        film.setBlock(frame, x, y, width, height, color)
        canvas.repaint()
    }

    override fun repaint() {
        super.repaint()
        canvas.repaint()
    }

    companion object {
        internal val steps = 100
    }
}
