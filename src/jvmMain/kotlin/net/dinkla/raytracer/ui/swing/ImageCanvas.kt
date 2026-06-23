package net.dinkla.raytracer.ui.swing

import java.awt.Dimension
import java.awt.Graphics
import java.awt.Image
import javax.swing.JPanel

private const val EMPTY_CANVAS_SIZE = 1

/**
 * Lightweight Swing component that draws the render [image]. As a [JPanel] it is double-buffered,
 * so repainting it repeatedly while the render is still filling the backing [java.awt.image.BufferedImage]
 * gives a smooth, flicker-free live preview.
 *
 * The component is embedded once in the main window and reused across renders: each render installs
 * its fresh film image via [image], so the preview swaps to the new render's pixels without spawning
 * a new window.
 */
internal class ImageCanvas : JPanel() {
    var image: Image? = null
        set(value) {
            field = value
            revalidate()
            repaint()
        }

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        image?.let { g.drawImage(it, 0, 0, this) }
    }

    override fun getPreferredSize(): Dimension {
        val img = image ?: return Dimension(EMPTY_CANVAS_SIZE, EMPTY_CANVAS_SIZE)
        return Dimension(img.getWidth(this), img.getHeight(this))
    }

    override fun getMinimumSize() = preferredSize
}
