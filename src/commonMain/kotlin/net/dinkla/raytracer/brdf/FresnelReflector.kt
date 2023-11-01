package net.dinkla.raytracer.brdf

import net.dinkla.raytracer.brdf.BRDF.Sample
import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.hits.IShade
import net.dinkla.raytracer.math.Vector3D

class FresnelReflector : BRDF {
    override fun f(sr: IShade, wo: Vector3D, wi: Vector3D): Color = Color.BLACK

    override fun rho(sr: IShade, wo: Vector3D): Color = Color.BLACK

    // TODO def
    override fun sampleF(sr: IShade, wo: Vector3D): Sample = Sample(
        wi = Vector3D.ZERO,
        color = Color.BLACK,
        pdf = 1.0
    )
}
