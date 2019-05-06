package net.dinkla.raytracer.world.dsl

import net.dinkla.raytracer.math.AffineTransformation
import net.dinkla.raytracer.math.Transformation

class InstanceScope(val trans : Transformation = AffineTransformation()) : Transformation by trans
