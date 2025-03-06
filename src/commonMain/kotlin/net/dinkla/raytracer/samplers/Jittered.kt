package net.dinkla.raytracer.samplers

import net.dinkla.raytracer.math.Point2D
import net.dinkla.raytracer.samplers.IGenerator.Companion.sqrt
import net.dinkla.raytracer.utilities.Random

object Jittered : IGenerator {
    override fun generateSamples(
        numSamples: Int,
        numSets: Int,
    ): List<Point2D> {
        val n = sqrt(numSamples)
        return buildList {
            for (p in 0 until numSets) {
                for (j in 0 until n) {
                    for (k in 0 until n) {
                        val x = (k + Random.double()) / n
                        val y = (j + Random.double()) / n
                        add(Point2D(x, y))
                    }
                }
            }
        }
    }
}
