package net.dinkla.raytracer.samplers

import net.dinkla.raytracer.math.Point2D

class HammersleyTest : AbstractGeneratorTest() {

    override fun sample(): MutableList<Point2D> =
            Hammersley.generateSamples(NUM_SAMPLES, NUM_SETS)

}
