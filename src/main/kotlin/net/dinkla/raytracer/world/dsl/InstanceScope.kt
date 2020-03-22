package net.dinkla.raytracer.world.dsl

import net.dinkla.raytracer.math.AffineTransformation
import net.dinkla.raytracer.math.Transformation

class InstanceScope(private val trans : Transformation = AffineTransformation()) : Transformation by trans
