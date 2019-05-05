package net.dinkla.raytracer.samplers

import net.dinkla.raytracer.math.Point2D
import net.dinkla.raytracer.samplers.IGenerator.Companion.sqrt

class JitteredTest : AbstractGeneratorTest() {

    override val numberOfSamples: Int = NUM_SETS * sqrt(NUM_SAMPLES) * sqrt(NUM_SAMPLES)

    override fun sample(): MutableList<Point2D> =
            Jittered.generateSamples(NUM_SAMPLES, NUM_SETS)
}
