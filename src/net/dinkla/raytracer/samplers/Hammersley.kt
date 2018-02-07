package net.dinkla.raytracer.samplers

import net.dinkla.raytracer.math.Point2D

class Hammersley : Generator() {

    protected fun phi(ij: Int): Double {
        var j = ij
        var x = 0.0
        var f = 0.5

        while (j > 0) {
            x += f * (j % 2)
            j /= 2
            f *= 0.5
        }

        return x
    }

    override fun generateSamples(numSamples: Int, numSets: Int, samples: MutableList<Point2D>) {
        for (p in 0 until numSets) {
            for (j in 0 until numSamples) {
                val pv = Point2D(j.toDouble() / numSamples.toDouble(), phi(j))
                samples.add(pv)
            }
        }
    }
}
