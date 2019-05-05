package net.dinkla.raytracer.samplers

import net.dinkla.raytracer.math.Point2D

class NRooksTest : AbstractGeneratorTest() {

    override fun sample(): MutableList<Point2D> =
            NRooks.generateSamples(NUM_SAMPLES, NUM_SETS)

}
