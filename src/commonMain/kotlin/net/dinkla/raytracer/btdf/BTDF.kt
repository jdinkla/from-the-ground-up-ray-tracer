package net.dinkla.raytracer.btdf

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.hits.IShade
import net.dinkla.raytracer.math.Vector3D

interface BTDF {
    class Sample(
        val color: Color = Color.WHITE,
        val wt: Vector3D = Vector3D.ZERO,
    )

    fun f(
        sr: IShade,
        wo: Vector3D,
        wi: Vector3D,
    ): Color

    fun sampleF(
        sr: IShade,
        wo: Vector3D,
    ): Sample

    fun rho(
        sr: IShade,
        wo: Vector3D,
    ): Color

    fun isTir(sr: IShade): Boolean
}
