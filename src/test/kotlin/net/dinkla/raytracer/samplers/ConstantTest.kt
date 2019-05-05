package net.dinkla.raytracer.samplers

import net.dinkla.raytracer.math.Point2D
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class ConstantTest : AbstractGeneratorTest() {

    override fun sample(): MutableList<Point2D> = Constant(0.2, 0.8).generateSamples(NUM_SAMPLES, NUM_SETS)

    override val sizeOfX = 1
    override val sizeOfY = 1
    override val sizeOfXY = 1

    @Test
    fun generateSamples() {
        val samples = Constant(0.2, 0.3).generateSamples(2, 3)
        assertEquals(2*3, samples.size)
        for (s in samples) {
            assertEquals(0.2, s.x)
            assertEquals(0.3, s.y)
        }
    }
}