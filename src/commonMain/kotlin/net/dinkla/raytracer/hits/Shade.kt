package net.dinkla.raytracer.hits

import net.dinkla.raytracer.materials.IMaterial
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.math.Vector3D

class Shade : Hit(), IShade {

    init {
        geometricObject = null
    }

    // for specular highlights, set by Tracer
    override var ray: Ray = Ray(Point3D.ORIGIN, Vector3D.ZERO)

    // Recursion depth, set by tracer
    override var depth: Int = 0

    override val hitPoint: Point3D
        get() = ray.linear(t)

    val localHitPoint: Point3D
        get() = ray.linear(t)

    override val material: IMaterial?
        get() = geometricObject?.material

    override fun toString(): String {
        return "Shade($ray, $depth, $material)"
    }
}
