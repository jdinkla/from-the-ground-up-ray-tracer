package net.dinkla.raytracer.gui.swing

import java.awt.*

internal class ImageCanvas(val img: Image) : Canvas() {

    override fun paint(g: Graphics): Unit {
        g.drawImage(img, 0, 0, this)
    }

    override fun getPreferredSize() = Dimension(img.getWidth(this), img.getHeight(this))

    override fun getMinimumSize() = preferredSize

}
