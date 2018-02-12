package net.dinkla.raytracer.samplers

import net.dinkla.raytracer.math.Point2D
import net.dinkla.raytracer.math.Random

class MultiJittered : Generator() {

    override fun generateSamples(numSamples: Int, numSets: Int, samples: MutableList<Point2D>) {
        val n = Math.sqrt(numSamples.toDouble()).toInt()
        val subcell_width = 1.0 / numSamples

        // distribute points in the initial patterns
        for (p in 0 until numSets) {
            for (i in 0 until n) {
                for (j in 0 until n) {
                    val target = i * n + j + p * numSamples
                    val x = (i * n + j) * subcell_width + Random.randFloat(0.0, subcell_width)
                    val y = (j * n + i) * subcell_width + Random.randFloat(0.0, subcell_width)
                    samples.add(target, Point2D(x, y))
                }
            }
        }

        // shuffle x coordinates
        for (p in 0 until numSets) {
            for (i in 0 until n) {
                for (j in 0 until n) {
                    val k = Random.randInt(j, n)
                    val source = i * n + j + p * numSamples
                    val target = i * n + k + p * numSamples
                    val temp = samples[source].x
                    samples[source] = Point2D(samples[target].x, samples[source].y)
                    samples[target] = Point2D(temp, samples[target].y)
                }
            }
        }

        // shuffle y coordinates
        for (p in 0 until numSets) {
            for (i in 0 until n) {
                for (j in 0 until n) {
                    val k = Random.randInt(j, n)
                    val target = k * n + i + p * numSamples
                    val source = j * n + i + p * numSamples
                    val temp = samples[source].y
                    samples[source] = Point2D(samples[source].x, samples[target].y)
                    samples[target] = Point2D(samples[target].x, temp)
                }
            }
        }
    }

}
