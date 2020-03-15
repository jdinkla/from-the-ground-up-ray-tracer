package net.dinkla.raytracer

import net.dinkla.raytracer.cameras.IColorCorrector
import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.utilities.AppProperties
import net.dinkla.raytracer.utilities.Resolution

class ViewPlane(val resolution: Resolution) : IColorCorrector {

    // Size of a pixel [build coordinates]
    var size: Double = 1.0
        private set(value: Double) {
            field = value
        }

    // Color correction
    var invGamma: Double = 1.0
        private set(value: Double) {
            field = value
        }

    var gamma: Double = 1.0
        set(value) {
            field = value
            this.invGamma = 1.0 / value
        }

    // Used for debugging
    private var showOutOfGamut: Boolean = false

    // maximal recursion depth
    var maxDepth: Int = 5
        private set(value: Int) {
            field = value
        }

    override fun correct(color: Color): Color {
        val newColor = if (showOutOfGamut) {
            color.clamp()
        } else {
            color.maxToOne()
        }
        if (gamma != 1.0) {
            return newColor.pow(invGamma)
        }
        return newColor
    }

    override fun toString(): String {
        return ("Viewplane: resolution=" + resolution + ", size=" + size + ", gamma=" + gamma
                + ", invGamma=" + invGamma + ", showOutOfGamut=" + showOutOfGamut
                + ", maxDepth=" + maxDepth)
    }

}
