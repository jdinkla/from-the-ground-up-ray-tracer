package net.dinkla.raytracer.samplers

import net.dinkla.raytracer.math.Point2D
import net.dinkla.raytracer.math.Random

class NRooks : IGenerator {

    override fun generateSamples(numSamples: Int, numSets: Int, samples: MutableList<Point2D>) {
        for (p in 0 until numSets) {
            for (j in 0 until numSamples) {
                val x = (j + Random.randFloat()) / numSamples
                val y = (j + Random.randFloat()) / numSamples
                samples.add(Point2D(x, y))
            }
        }

        Sampler.shuffleXCoordinates(numSamples, numSets, samples)
        Sampler.shuffleYCoordinates(numSamples, numSets, samples)
    }

}