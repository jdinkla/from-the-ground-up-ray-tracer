package net.dinkla.raytracer.samplers

import net.dinkla.raytracer.math.Point2D

class RegularTest : AbstractGeneratorTest() {

    override val numberOfSamples: Int = NUM_SETS * IGenerator.sqrt(NUM_SAMPLES) * IGenerator.sqrt(NUM_SAMPLES)

    override fun sample(): MutableList<Point2D> =
            Regular.generateSamples(NUM_SAMPLES, NUM_SETS)

}
