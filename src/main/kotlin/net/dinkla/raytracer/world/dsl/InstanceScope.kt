package net.dinkla.raytracer.world.dsl

import net.dinkla.raytracer.math.AffineTransformation
import net.dinkla.raytracer.math.Transformation
import net.dinkla.raytracer.math.Vector3D

class InstanceScope(val trans : Transformation = AffineTransformation()) : Transformation by trans {

}