package net.dinkla.raytracer.gui.swing

import net.dinkla.raytracer.gui.awt.AwtFilm
import net.dinkla.raytracer.gui.awt.ImageCanvas
import net.dinkla.raytracer.utilities.Resolution
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import javax.swing.JFrame

class ImageFrame private constructor(film: AwtFilm, protected val isMainFrame: Boolean) : JFrame() {

    private val canvas = ImageCanvas(film.image)

    constructor(film: AwtFilm) : this(film, false) {
        add(canvas)
        setSize(film.resolution.hres, film.resolution.vres + 22)
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

//    override val resolution: Resolution
//        get() = film.resolution

//    override fun setPixel(x: Int, y: Int, color: Color) {
//        film.setPixel(x, y, color)
//        canvas.repaint()
//    }
//
//    override fun setBlock(x: Int, y: Int, width: Int, height: Int, color: Color) {
//        film.setBlock(x, y, width, height, color)
//        canvas.repaint()
//    }

    override fun repaint() {
        super.repaint()
        canvas.repaint()
    }

}
