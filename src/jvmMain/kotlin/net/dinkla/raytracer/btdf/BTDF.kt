package net.dinkla.raytracer.btdf

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.hits.Shade
import net.dinkla.raytracer.math.Vector3D
import net.dinkla.raytracer.samplers.Sampler

interface BTDF {

    class Sample(
            val color: Color = Color.WHITE,
            val wt: Vector3D = Vector3D.ZERO
    )

    fun f(sr: Shade, wo: Vector3D, wi: Vector3D): Color
    fun sampleF(sr: Shade, wo: Vector3D): Sample
    fun rho(sr: Shade, wo: Vector3D): Color
    fun isTir(sr: Shade): Boolean
}
