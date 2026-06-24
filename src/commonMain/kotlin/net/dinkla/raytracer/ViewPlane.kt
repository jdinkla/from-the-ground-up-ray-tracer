package net.dinkla.raytracer

import net.dinkla.raytracer.cameras.IColorCorrector
import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.utilities.Resolution

class ViewPlane : IColorCorrector {
    var resolution = Resolution(1080)

    var sizeOfPixel: Double = 1.0
        private set

    /**
     * Switches the view plane to [newResolution] while preserving the visible world extent — the
     * field of view. The view plane's world-space size is `sizeOfPixel * resolution`, so changing the
     * pixel count alone would zoom the camera (lower resolutions zoom in, higher ones zoom out; see
     * TASK-36). Rescaling [sizeOfPixel] inversely to the height change keeps `sizeOfPixel * height`
     * invariant; since every resolution here shares the 16:9 aspect ratio, the width extent is
     * preserved too. The render pipeline calls this from
     * [net.dinkla.raytracer.world.Context.adapt] so a scene renders the same framing at any
     * resolution, differing only in sampling density. (Assign [resolution] directly only to define a
     * baseline — e.g. constructing a fixed-size view plane in a test — where no rescaling is wanted.)
     */
    fun applyResolution(newResolution: Resolution) {
        sizeOfPixel *= resolution.height.toDouble() / newResolution.height
        resolution = newResolution
    }

    private var gamma: Double = 1.0

    private var showOutOfGamutForDebugging: Boolean = false

    /**
     * Maximum recursion depth for the tracer (reflection/refraction bounces). Defaults to `5`, which
     * suits every scene that does not nest transparent media. Deeply nested dielectrics (e.g. three
     * concentric glass shells) need a higher limit so the innermost transmitted rays reach a surface
     * instead of being truncated to the background; set it from the scene DSL via
     * [net.dinkla.raytracer.world.dsl.WorldScope.maxDepth]. The setter is `internal` so only the
     * render core (the DSL) can raise it; from outside the module it is read-only.
     */
    var maximalRecursionDepth: Int = 5
        internal set

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
