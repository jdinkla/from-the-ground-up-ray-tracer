package net.dinkla.raytracer.gui.swing

import java.awt.*

internal class ImageCanvas(var img: Image) : Canvas() {

    override fun paint(g: Graphics) {
        g.drawImage(img, 0, 0, this)
    }

    override fun getPreferredSize(): Dimension {
        return Dimension(img.getWidth(this), img.getHeight(this))
    }

    override fun getMinimumSize(): Dimension {
        return preferredSize
    }

}
