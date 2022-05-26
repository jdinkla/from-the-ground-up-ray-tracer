package net.dinkla.raytracer.math

interface Transformation {

    val forwardMatrix: Matrix
    val invMatrix: Matrix

    fun translate(v: Vector3D)
    fun scale(v: Vector3D)
    fun rotate(axis: Axis, phi: Double)
    fun shear(s: Matrix)

    fun ray(ray: Ray): Ray
}