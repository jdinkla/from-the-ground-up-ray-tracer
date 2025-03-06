package net.dinkla.raytracer.samplers

import net.dinkla.raytracer.math.Point2D
import net.dinkla.raytracer.samplers.IGenerator.Companion.generate2D

object Hammersley : IGenerator {
    override fun generateSamples(
        numSamples: Int,
        numSets: Int,
    ): List<Point2D> {
        val numSamplesD = numSamples.toDouble()
        return generate2D(numSamples, numSets) { _, j ->
            Point2D(j.toDouble() / numSamplesD, phi(j))
        }
    }

    private fun phi(ij: Int): Double {
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
}
