package net.dinkla.raytracer.math

data class Ray(
    val origin: Point3D,
    val direction: Vector3D,
) {
    constructor(ray: Ray) : this(ray.origin, ray.direction)

    fun linear(t: Double): Point3D = origin + (direction * t)
}
