package net.dinkla.raytracer.samplers

import net.dinkla.raytracer.math.Point2D

class PureRandomTest : AbstractGeneratorTest() {

    override fun sample(): MutableList<Point2D> = PureRandom.generateSamples(NUM_SAMPLES, NUM_SETS)

}
