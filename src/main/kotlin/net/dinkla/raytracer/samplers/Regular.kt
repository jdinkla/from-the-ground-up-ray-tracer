package net.dinkla.raytracer.samplers

import net.dinkla.raytracer.math.Point2D

object Regular : IGenerator {

    override fun generateSamples(numSamples: Int, numSets: Int): MutableList<Point2D> {
        val samples = mutableListOf<Point2D>()
        val n = IGenerator.sqrt(numSamples)
        for (j in 0 until numSets) {
            for (p in 0 until n) {
                for (q in 0 until n) {
                    samples.add(Point2D((q + 0.5) / n, (p + 0.5) / n))
                }
            }
        }
        return samples
    }
}
