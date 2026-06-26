package net.dinkla.raytracer.btdf

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.hits.IShade
import net.dinkla.raytracer.math.Vector3D

/**
 * A bidirectional transmittance distribution function (Suffern ch. 27/28). Both implementations
 * ([PerfectTransmitter], [FresnelTransmitter]) are perfectly specular, so — unlike a BRDF — there is
 * no evaluable `f` or bihemispherical `rho`: a transmitted ray is only ever importance-sampled
 * ([sampleF]) after checking for total internal reflection ([isTir]). The interface therefore
 * declares exactly those two operations (TASK-63: it previously also declared `f`/`rho`, which every
 * implementation could only stub with UnsupportedOperationException and no caller ever used).
 */
interface BTDF {
    class Sample(
        val color: Color = Color.WHITE,
        val wt: Vector3D = Vector3D.ZERO,
    )

    fun sampleF(
        sr: IShade,
        wo: Vector3D,
    ): Sample

    fun isTir(sr: IShade): Boolean
}
