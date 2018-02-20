package net.dinkla.raytracer.samplers

import net.dinkla.raytracer.math.Point2D

class Constant : IGenerator {

    override fun generateSamples(numSamples: Int, numSets: Int, samples: MutableList<Point2D>) {
        val n = Math.sqrt(numSamples.toDouble()).toInt()
        for (j in 0 until numSets) {
            for (p in 0 until numSamples) {
                samples.add(Point2D(0.5, 0.5))
            }
        }
    }

}
