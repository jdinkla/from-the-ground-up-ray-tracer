package net.dinkla.raytracer.samplers

import net.dinkla.raytracer.math.Point2D
import net.dinkla.raytracer.utilities.Random

object NRooks : IGenerator {

    override fun generateSamples(numSamples: Int, numSets: Int): List<Point2D> {
        val samples = buildList<Point2D>() {
            for (p in 0 until numSets) {
                for (j in 0 until numSamples) {
                    val x = (j + Random.double()) / numSamples
                    val y = (j + Random.double()) / numSamples
                    add(Point2D(x, y))
                }
            }
        }
        val mutableSamples = samples.toMutableList()
        Sampler.shuffleXCoordinates(numSamples, numSets, mutableSamples)
        Sampler.shuffleYCoordinates(numSamples, numSets, mutableSamples)
        return mutableSamples
    }

}