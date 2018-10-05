package net.dinkla.raytracer.samplers

import net.dinkla.raytracer.math.Histogram
import net.dinkla.raytracer.math.Point2D
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

abstract class IGeneratorTest {

    protected val samples: MutableList<Point2D> = mutableListOf()

    @BeforeEach
    abstract fun initialize()

    @Test
    fun size() {
        assertEquals(samples.size, NUM_SAMPLES * NUM_SETS)
    }

    @Test
    fun betweenZeroAndOne() {
        for (p in samples) {
            assert(p.x >= 0.0 && p.x < 1.0)
            assert(p.y >= 0.0 && p.y < 1.0)
        }
    }

    @Test
    fun distribution() {
        val histX = Histogram()
        val histY = Histogram()
        val histXY = Histogram()

        for (p in samples) {
            val x = (p.x * 10).toInt()
            val y = (p.y * 10).toInt()
            histX.add(x)
            histY.add(y)
            histXY.add(x * 10 + y)
        }

        println("--- X ---")
        histX.println()

        println("--- Y ---")
        histY.println()

        println("--- XY ---")
        histXY.println()
    }

    companion object {
        internal val NUM_SAMPLES = 10000
        internal val NUM_SETS = 10
    }

}
