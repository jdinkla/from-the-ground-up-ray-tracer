package net.dinkla.raytracer.samplers

import net.dinkla.raytracer.math.Point2D
import net.dinkla.raytracer.interfaces.Random
import net.dinkla.raytracer.samplers.IGenerator.Companion.sqrt

object Jittered : IGenerator {

    override fun generateSamples(numSamples: Int, numSets: Int): MutableList<Point2D> {
        val samples = mutableListOf<Point2D>()
        val n = sqrt(numSamples)
        for (p in 0 until numSets) {
            for (j in 0 until n) {
                for (k in 0 until n) {
                    val x = (k + Random.double()) / n
                    val y = (j + Random.double()) / n
                    samples.add(Point2D(x, y))
                }
            }
        }
        return samples
    }

}
