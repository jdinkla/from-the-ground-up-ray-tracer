package net.dinkla.raytracer.btdf

import net.dinkla.raytracer.btdf.BTDF.Sample
import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.hits.IShade
import net.dinkla.raytracer.math.Vector3D

class FresnelTransmitter : BTDF {

    override fun f(sr: IShade, wo: Vector3D, wi: Vector3D): Color = Color.BLACK

    override fun isTir(sr: IShade): Boolean = false

    override fun rho(sr: IShade, wo: Vector3D): Color = Color.BLACK

    override fun sampleF(sr: IShade, wo: Vector3D): Sample = Sample()

}
