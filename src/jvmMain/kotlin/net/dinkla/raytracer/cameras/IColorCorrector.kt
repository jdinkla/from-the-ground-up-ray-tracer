package net.dinkla.raytracer.cameras

import net.dinkla.raytracer.colors.Color

interface IColorCorrector {

    fun correct(color: Color): Color

}
