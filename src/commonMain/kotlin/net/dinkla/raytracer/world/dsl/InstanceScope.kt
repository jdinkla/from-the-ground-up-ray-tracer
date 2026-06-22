package net.dinkla.raytracer.world.dsl

import net.dinkla.raytracer.math.AffineTransformation
import net.dinkla.raytracer.math.Transformation

/**
 * DSL receiver for the `instance(...) { ... }` block. It delegates to a [Transformation] (the placed
 * Instance's affine transformation by default), so the block can call the transformation builders
 * (`translate`, `rotate`, `scale`, …) directly on the instance being placed.
 */
class InstanceScope(
    private val trans: Transformation = AffineTransformation(),
) : Transformation by trans
