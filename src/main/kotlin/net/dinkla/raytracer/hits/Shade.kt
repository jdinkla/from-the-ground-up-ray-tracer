package net.dinkla.raytracer.hits

import net.dinkla.raytracer.materials.IMaterial
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.math.Vector3D

class Shade : Hit() {

    // for specular highlights, set by Tracer
    var ray: Ray

    // Recursion depth, set by tracer
    var depth: Int = 0

    val hitPoint: Point3D
        get() = ray.linear(t)

    val localHitPoint: Point3D
        get() = ray.linear(t)

    val material: IMaterial?
        get() = geometricObject!!.material

    init {
        this.depth = 0
        this.ray = Ray(Point3D.ORIGIN, Vector3D.ZERO)
        this.geometricObject = null
    }

}
