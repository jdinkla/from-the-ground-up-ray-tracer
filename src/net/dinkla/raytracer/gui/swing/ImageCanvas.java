package net.dinkla.raytracer.gui.swing;

/*
 * Copyright (c) 2012 by Jörn Dinkla, www.dinkla.com, All rights reserved.
 */

import java.awt.*;

class ImageCanvas extends Canvas {
    Image img;

    ImageCanvas(final Image img) {
        this.img = img;
    }

    public void paint(final Graphics g) {
        g.drawImage(img, 0, 0, this);
    }

    public Dimension getPreferredSize() {
        return new Dimension(img.getWidth(this), img.getHeight(this));
    }

    public Dimension getMinimumSize() {
        return getPreferredSize();
    }

}
