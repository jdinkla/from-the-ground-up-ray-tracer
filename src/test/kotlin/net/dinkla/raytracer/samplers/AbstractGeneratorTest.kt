package net.dinkla.raytracer.samplers

import net.dinkla.raytracer.math.Histogram
import net.dinkla.raytracer.math.Point2D
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

abstract class AbstractGeneratorTest {

    private var samples: MutableList<Point2D> = mutableListOf()

    private val factorX = 10.0
    private val factorY = 10.0
    open val sizeOfX : Int = factorX.toInt()
    open val sizeOfY : Int = factorY.toInt()
    open val sizeOfXY : Int = factorX.toInt() * factorY.toInt()
    open val numberOfSamples: Int = NUM_SAMPLES * NUM_SETS

    abstract fun sample(): MutableList<Point2D>

    @BeforeEach
    fun initialize() {
        samples = sample()
    }

    @Test
    fun size() {
        assertEquals(numberOfSamples, samples.size)
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
            val x = (p.x * factorX).toInt()
            val y = (p.y * factorY).toInt()
            histX.add(x)
            histY.add(y)
            histXY.add(x * 10 + y)
        }

        assertEquals(sizeOfX, histX.keys().size)
        assertEquals(sizeOfY, histY.keys().size)
        assertEquals(sizeOfXY, histXY.keys().size)
    }

    companion object {
        internal const val NUM_SAMPLES = 1000
        internal const val NUM_SETS = 10
    }

}
