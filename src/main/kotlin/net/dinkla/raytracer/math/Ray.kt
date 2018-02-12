package net.dinkla.raytracer.math

/**
 * Created by IntelliJ IDEA.
 * User: jorndinkla
 * Date: 10.04.2010
 * Time: 15:28:13
 * To change this template use File | Settings | File Templates.
 */
class Ray {

    val o: Point3D
    val d: Vector3D

    constructor(origin: Point3D, direction: Vector3D) {
        this.o = origin
        this.d = direction
    }

    constructor(ray: Ray) {
        this.o = ray.o
        this.d = ray.d
    }

    fun linear(t: Double): Point3D {
        return o + (d * t)
    }

    override fun toString(): String {
        return "Ray(" + o.toString() + ", " + d.toString() + ")"
    }

    companion object {
        // TODO needed?
        val DEFAULT = Ray(Point3D.ORIGIN, Vector3D.ZERO)
    }

}
