package net.dinkla.raytracer

import net.dinkla.raytracer.cameras.IColorCorrector
import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.utilities.Resolution

class ViewPlane : IColorCorrector {
    var resolution = Resolution(1080)

    var sizeOfPixel: Double = 1.0
        private set

    private var gamma: Double = 1.0

    private var showOutOfGamutForDebugging: Boolean = false

    var maximalRecursionDepth: Int = 5
        private set

    override fun correct(color: Color): Color {
        val newColor = if (showOutOfGamutForDebugging) {
            color.clamp()
        } else {
            color.maxToOne()
        }
        if (gamma != 1.0) {
            return newColor.pow(1.0 / gamma)
        }
        return newColor
    }

    override fun toString(): String {
        return ("Viewplane: resolution=$resolution, sizeOfPixel=$sizeOfPixel, "
                + "gamma=$gamma, showOutOfGamut=$showOutOfGamutForDebugging, maxDepth=$maximalRecursionDepth")
    }
}
