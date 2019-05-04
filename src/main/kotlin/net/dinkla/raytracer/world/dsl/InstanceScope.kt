package net.dinkla.raytracer.world.dsl

import net.dinkla.raytracer.math.AffineTransformation
import net.dinkla.raytracer.math.ITransformation
import net.dinkla.raytracer.math.Vector3D

class InstanceScope(val trans : ITransformation = AffineTransformation()) {

    fun translate(v: Vector3D) {
        trans.translate(v)
    }

}