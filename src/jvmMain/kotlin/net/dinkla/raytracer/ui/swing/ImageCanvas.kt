package net.dinkla.raytracer.ui.swing

import java.awt.Canvas
import java.awt.Dimension
import java.awt.Graphics
import java.awt.Image

internal class ImageCanvas(private val image: Image) : Canvas() {
    override fun paint(g: Graphics) {
        g.drawImage(image, 0, 0, this)
    }

    override fun getPreferredSize() = Dimension(image.getWidth(this), image.getHeight(this))

    override fun getMinimumSize() = preferredSize
}
