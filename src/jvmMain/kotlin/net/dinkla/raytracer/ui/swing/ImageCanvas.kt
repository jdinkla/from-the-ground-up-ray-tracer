package net.dinkla.raytracer.ui.swing

import java.awt.Dimension
import java.awt.Graphics
import java.awt.Image
import javax.swing.JPanel

/**
 * Lightweight Swing component that draws the render [image]. As a [JPanel] it is double-buffered,
 * so repainting it repeatedly while the render is still filling the backing [java.awt.image.BufferedImage]
 * gives a smooth, flicker-free live preview.
 */
internal class ImageCanvas(
    private val image: Image,
) : JPanel() {
    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        g.drawImage(image, 0, 0, this)
    }

    override fun getPreferredSize() = Dimension(image.getWidth(this), image.getHeight(this))

    override fun getMinimumSize() = preferredSize
}
