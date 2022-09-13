package net.dinkla.raytracer.gui.swing

import net.dinkla.raytracer.gui.awt.AwtFilm
import net.dinkla.raytracer.gui.awt.ImageCanvas
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import javax.swing.JFrame

class ImageFrame private constructor(film: AwtFilm, private val isMainFrame: Boolean) : JFrame() {

    private val canvas = ImageCanvas(film.image)

    constructor(film: AwtFilm) : this(film, false) {
        add(canvas)
        setSize(film.resolution.hres, film.resolution.vres + 22)
        addWindowListener(windowAdapter)
        isVisible = true
    }

    override fun repaint() {
        super.repaint()
        canvas.repaint()
    }

    private val windowAdapter = object : WindowAdapter() {
        override fun windowClosing(ev: WindowEvent?) {
            dispose()
            if (isMainFrame) {
                System.exit(0)
            }
        }
    }
}
