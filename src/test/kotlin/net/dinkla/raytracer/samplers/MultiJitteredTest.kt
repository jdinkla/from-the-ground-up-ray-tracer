package net.dinkla.raytracer.samplers

import net.dinkla.raytracer.math.Point2D

class MultiJitteredTest : AbstractGeneratorTest() {

    override val sizeOfXY: Int = 100
    override val numberOfSamples: Int = NUM_SETS * IGenerator.sqrt(NUM_SAMPLES) * IGenerator.sqrt(NUM_SAMPLES)  + 1

    override fun sample(): MutableList<Point2D> =
            MultiJittered.generateSamples(NUM_SAMPLES, NUM_SETS)

}
