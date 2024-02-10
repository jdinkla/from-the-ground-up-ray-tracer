package net.dinkla.raytracer.samplers

import net.dinkla.raytracer.math.Point2D

interface IGenerator {

    fun generateSamples(numSamples: Int, numSets: Int): List<Point2D>

    companion object {
        fun sqrt(numSamples: Int) = kotlin.math.sqrt(numSamples.toDouble()).toInt()

        fun generate2D(numSamples: Int, numSets: Int, sample: (Int, Int) -> Point2D) =
            buildList {
                for (j in 0 until numSets) {
                    for (p in 0 until numSamples) {
                        add(sample(j, p))
                    }
                }
            }
    }
}
