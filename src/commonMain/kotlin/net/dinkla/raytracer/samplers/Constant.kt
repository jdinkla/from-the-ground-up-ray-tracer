package net.dinkla.raytracer.samplers

import net.dinkla.raytracer.math.Point2D
import net.dinkla.raytracer.samplers.IGenerator.Companion.generate2D

class Constant(val x: Double = 0.5, val y: Double = 0.5) : IGenerator {
    override fun generateSamples(numSamples: Int, numSets: Int): List<Point2D> =
        generate2D(numSamples, numSets) { _, _ -> Point2D(x, y) }
}
