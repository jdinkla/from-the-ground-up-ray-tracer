package net.dinkla.raytracer.samplers

import net.dinkla.raytracer.math.Point2D
import net.dinkla.raytracer.utilities.Random

object PureRandom : IGenerator {

    override fun generateSamples(numSamples: Int, numSets: Int): MutableList<Point2D> {
        val samples = mutableListOf<Point2D>()
        for (p in 0 until numSets) {
            for (q in 0 until numSamples) {
                samples.add(Point2D(Random.double(), Random.double()))
            }
        }
        return samples
    }

}
