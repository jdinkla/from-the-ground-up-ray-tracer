package net.dinkla.raytracer.gui.swing

/*
 * Copyright (c) 2012 by Jörn Dinkla, www.dinkla.com, All rights reserved.
 */

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.films.BufferedImageFilm
import net.dinkla.raytracer.films.IFilm
import net.dinkla.raytracer.utilities.Resolution

import javax.swing.*

class ImagePanel(resolution: Resolution) : JPanel(), IFilm {

    val film: BufferedImageFilm
    private val canvas: ImageCanvas

    override val resolution: Resolution
        get() = film.resolution

    init {
        film = BufferedImageFilm()
        film.initialize(1, resolution)
        canvas = ImageCanvas(film.img)
        add(canvas)
    }

    override fun initialize(numFrames: Int, resolution: Resolution) = film.initialize(numFrames, resolution)

    override fun finish() = film.finish()

    override fun setPixel(frame: Int, x: Int, y: Int, color: Color) {
        film.setPixel(frame, x, y, color)
        canvas.repaint()
    }

    override fun setBlock(frame: Int, x: Int, y: Int, width: Int, height: Int, color: Color) {
        film.setBlock(frame, x, y, width, height, color)
        canvas.repaint()
    }

}
