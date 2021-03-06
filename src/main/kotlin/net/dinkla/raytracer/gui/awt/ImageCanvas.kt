package net.dinkla.raytracer.gui.awt

import java.awt.Canvas
import java.awt.Dimension
import java.awt.Graphics
import java.awt.Image

internal class ImageCanvas(private val img: Image) : Canvas() {

    override fun paint(g: Graphics): Unit {
        g.drawImage(img, 0, 0, this)
    }

    override fun getPreferredSize() = Dimension(img.getWidth(this), img.getHeight(this))

    override fun getMinimumSize() = preferredSize

}
