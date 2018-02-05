package net.dinkla.raytracer.samplers

import net.dinkla.raytracer.math.Point2D

class Hammersley : Generator() {

    protected fun phi(j: Int): Float {
        var j = j
        var x = 0.0f
        var f = 0.5f

        while (j > 0) {
            x += f * (j % 2).toFloat()
            j /= 2
            f *= 0.5f
        }

        return x
    }

    override fun generateSamples(numSamples: Int, numSets: Int, samples: MutableList<Point2D>) {
        for (p in 0 until numSets) {
            for (j in 0 until numSamples) {
                val pv = Point2D(j.toFloat() / numSamples.toFloat(), phi(j))
                samples.add(pv)
            }
        }
    }
}
