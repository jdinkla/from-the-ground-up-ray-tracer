package net.dinkla.raytracer.samplers

import net.dinkla.raytracer.math.Point2D
import net.dinkla.raytracer.math.Random

class Jittered : IGenerator {

    override fun generateSamples(numSamples: Int, numSets: Int, samples: MutableList<Point2D>) {
        val n = Math.sqrt(numSamples.toDouble()).toInt()
        for (p in 0 until numSets) {
            for (j in 0 until n) {
                for (k in 0 until n) {
                    val x = (k + Random.randFloat()) / n
                    val y = (j + Random.randFloat()) / n
                    samples.add(Point2D(x, y))
                }
            }
        }
    }

}
