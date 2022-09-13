package net.dinkla.raytracer.btdf

import net.dinkla.raytracer.btdf.BTDF.Sample
import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.hits.Shade
import net.dinkla.raytracer.math.Vector3D

class FresnelTransmitter : BTDF {

    override fun f(sr: Shade, wo: Vector3D, wi: Vector3D): Color = Color.BLACK

    override fun isTir(sr: Shade): Boolean = false

    override fun rho(sr: Shade, wo: Vector3D): Color = Color.BLACK

    override fun sampleF(sr: Shade, wo: Vector3D): Sample = Sample()

}
