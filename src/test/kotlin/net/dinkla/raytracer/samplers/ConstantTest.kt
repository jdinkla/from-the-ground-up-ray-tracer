package net.dinkla.raytracer.samplers

import io.kotest.matchers.shouldBe
import net.dinkla.raytracer.math.Point2D

internal class ConstantTest : AbstractGeneratorTest() {

    override fun sample(): MutableList<Point2D> = Constant(0.2, 0.8).generateSamples(NUM_SAMPLES, NUM_SETS)

    override val sizeOfX = 1
    override val sizeOfY = 1
    override val sizeOfXY = 1

    @Test
    fun generateSamples() {
        val samples = Constant(0.2, 0.3).generateSamples(2, 3)
        samples.size shouldBe 2*3
        for (s in samples) {
            s.x shouldBe 0.2
            s.y shouldBe 0.3
        }
    }
}