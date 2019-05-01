package net.dinkla.raytracer

import net.dinkla.raytracer.cameras.IColorCorrector
import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.utilities.AppProperties
import net.dinkla.raytracer.utilities.Resolution

class ViewPlane(val resolution: Resolution) : IColorCorrector {

    // Size of a pixel [build coordinates]
    var size: Double = 0.toDouble()

    // Color correction
    private var gamma: Double = 0.toDouble()
    private var invGamma: Double = 0.toDouble()

    // Used for debugging
    var showOutOfGamut: Boolean = false

    // maximal recursion depth
    var maxDepth: Int = 0

    init {
        this.size = 1.0
        this.gamma = 1.0
        this.invGamma = 1.0
        this.showOutOfGamut = false
        this.maxDepth = 5
    }

    fun getGamma(): Double {
        return gamma
    }

    fun setGamma(gamma: Double) {
        this.gamma = gamma
        this.invGamma = 1.0 / gamma
    }

    override fun correct(color: Color): Color {
        var newColor: Color
        if (showOutOfGamut) {
            newColor = color.clamp()
        } else {
            newColor = color.maxToOne()
        }
        if (gamma != 1.0) {
            newColor = newColor.pow(invGamma)
        }
        return newColor
    }

    override fun toString(): String {
        return ("Viewplane: resolution=" + resolution + ", size=" + size + ", gamma=" + gamma
                + ", invGamma=" + invGamma + ", showOutOfGamut=" + showOutOfGamut
                + ", maxDepth=" + maxDepth)
    }

}
