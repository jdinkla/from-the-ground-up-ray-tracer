package net.dinkla.raytracer.samplers

import net.dinkla.raytracer.math.Point2D

class Constant(val x: Double = 0.5, val y: Double = 0.5) : IGenerator {

    override fun generateSamples(numSamples: Int, numSets: Int): MutableList<Point2D> =
            generate2D(numSamples, numSets) { _, _ -> Point2D(x, y) }

}
