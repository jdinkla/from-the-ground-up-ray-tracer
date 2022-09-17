package net.dinkla.raytracer.gui.swing

import net.dinkla.raytracer.films.Film
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import javax.swing.JFrame

class ImageFrame private constructor(film: Film, private val isMainFrame: Boolean) : JFrame() {

    private val canvas = ImageCanvas(film.image)

    constructor(film: Film) : this(film, false) {
        add(canvas)
        setSize(film.resolution.height, film.resolution.width + 22)
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
