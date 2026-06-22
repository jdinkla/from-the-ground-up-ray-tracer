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

    /**
     * Number of primary samples cast per pixel. `1` (the default) means a single ray through the
     * pixel centre — no anti-aliasing, the historical behaviour every scene relies on. Values `> 1`
     * opt the scene into multi-sample anti-aliasing (and, with a [net.dinkla.raytracer.cameras.lenses.ThinLens],
     * visible depth-of-field blur): the render pipeline selects the sampled single-ray renderer and
     * averages this many jittered samples per pixel (see [net.dinkla.raytracer.world.Context.adapt]).
     */
    var numSamples: Int = 1

    override fun correct(color: Color): Color {
        val newColor =
            if (showOutOfGamutForDebugging) {
                color.clamp()
            } else {
                color.maxToOne()
            }
        if (gamma != 1.0) {
            return newColor.pow(1.0 / gamma)
        }
        return newColor
    }

    override fun toString(): String =
        (
            "Viewplane: resolution=$resolution, sizeOfPixel=$sizeOfPixel, " +
                "gamma=$gamma, showOutOfGamut=$showOutOfGamutForDebugging, " +
                "maxDepth=$maximalRecursionDepth, numSamples=$numSamples"
        )
}
