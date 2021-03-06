package net.dinkla.raytracer

import net.dinkla.raytracer.cameras.IColorCorrector
import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.utilities.Resolution

class ViewPlane(val resolution: Resolution) : IColorCorrector {
    var sizeOfPixel: Double = 1.0
        private set

    var gamma: Double = 1.0
        private set

    private var showOutOfGamutForDebugging: Boolean = false

    // maximal recursion depth
    var maxDepth: Int = 5
        private set(value: Int) {
            field = value
        }

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
        return ("Viewplane: resolution=$resolution, sizeOfPixel=$sizeOfPixel, gamma=$gamma, showOutOfGamut=$showOutOfGamutForDebugging, maxDepth=$maxDepth")
    }
}
