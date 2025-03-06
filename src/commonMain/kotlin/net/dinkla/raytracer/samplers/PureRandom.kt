package net.dinkla.raytracer.samplers

import net.dinkla.raytracer.math.Point2D
import net.dinkla.raytracer.utilities.Random

object PureRandom : IGenerator {
    override fun generateSamples(
        numSamples: Int,
        numSets: Int,
    ): List<Point2D> =
        buildList {
            for (p in 0 until numSets) {
                for (q in 0 until numSamples) {
                    add(Point2D(Random.double(), Random.double()))
                }
            }
        }
}
