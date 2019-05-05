package net.dinkla.raytracer.math

interface Transformation {

    val forwardMatrix: Matrix
    val invMatrix: Matrix

    fun translate(v: Vector3D)
    fun translate(x: Double, y: Double, z: Double)
    fun scale(v: Vector3D)
    fun scale(x: Double, y: Double, z: Double)
    fun rotateX(phi: Double)
    fun rotateY(phi: Double)
    fun rotateZ(phi: Double)
    fun shear(s: Matrix)

    fun ray(ray: Ray): Ray
}