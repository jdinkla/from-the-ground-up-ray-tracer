package net.dinkla.raytracer.cameras.lenses

import net.dinkla.raytracer.ViewPlane
import net.dinkla.raytracer.math.Basis
import net.dinkla.raytracer.math.Point2D
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.math.Vector3D
import net.dinkla.raytracer.samplers.Sampler

class ThinLens(
    viewPlane: ViewPlane,
    eye: Point3D,
    uvw: Basis,
) : AbstractLens(viewPlane, eye, uvw) {
    var sampler: Sampler? = null

    var f: Double = 1.0
    var d: Double = 1.0

    override fun getRaySingle(
        r: Int,
        c: Int,
    ): Ray = getRay()

    override fun getRaySampled(
        r: Int,
        c: Int,
        sp: Point2D,
    ): Ray = getRay()

    private fun getRay(): Ray = Ray(eye, getRayDirection())

    private fun getRayDirection(): Vector3D = uvw.pm(1.0, 1.0, 1.0).normalize()
}
