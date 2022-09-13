package net.dinkla.raytracer.samplers

import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.doubles.shouldBeGreaterThanOrEqual
import io.kotest.matchers.doubles.shouldBeLessThan
import io.kotest.matchers.shouldBe
import net.dinkla.raytracer.math.Histogram
import net.dinkla.raytracer.math.Point2D

abstract class AbstractGeneratorTest : AnnotationSpec() {

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
        samples.size shouldBe numberOfSamples
    }

    @Test
    fun betweenZeroAndOne() {
        for (p in samples) {
            p.x shouldBeGreaterThanOrEqual 0.0
            p.x shouldBeLessThan 1.0
            p.y shouldBeGreaterThanOrEqual 0.0
            p.y shouldBeLessThan 1.0
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

        histX.keys().size shouldBe sizeOfX
        histY.keys().size shouldBe sizeOfY
        histXY.keys().size shouldBe sizeOfXY
    }

    companion object {
        internal const val NUM_SAMPLES = 1000
        internal const val NUM_SETS = 10
    }

}
