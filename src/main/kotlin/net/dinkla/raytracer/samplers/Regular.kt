package net.dinkla.raytracer.samplers

import net.dinkla.raytracer.math.Point2D

class Regular : Generator() {

    override fun generateSamples(numSamples: Int, numSets: Int, samples: MutableList<Point2D>) {
        val n = Math.sqrt(numSamples.toDouble()).toInt()
        for (j in 0 until numSets) {
            for (p in 0 until n) {
                for (q in 0 until n) {
                    samples.add(Point2D((q + 0.5) / n, (p + 0.5) / n))
                }
            }
        }

    }
}
