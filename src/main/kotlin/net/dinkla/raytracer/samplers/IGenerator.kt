package net.dinkla.raytracer.samplers

import net.dinkla.raytracer.math.Point2D

interface IGenerator {

    fun generateSamples(numSamples: Int, numSets: Int) : MutableList<Point2D>

    fun generate2D(numSamples: Int, numSets: Int, sample: (Int, Int) -> Point2D): MutableList<Point2D> {
        val samples = mutableListOf<Point2D>()
        for (j in 0 until numSets) {
            for (p in 0 until numSamples) {
                samples.add(sample(j, p))
            }
        }
        return samples
    }

    companion object {
        fun sqrt(numSamples: Int) = Math.sqrt(numSamples.toDouble()).toInt()
    }

}
