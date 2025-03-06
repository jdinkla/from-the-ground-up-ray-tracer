package net.dinkla.raytracer.samplers

import io.kotest.core.spec.style.stringSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.doubles.shouldBeGreaterThanOrEqual
import io.kotest.matchers.doubles.shouldBeLessThan
import net.dinkla.raytracer.math.Point2D
import net.dinkla.raytracer.utilities.Histogram

fun Point2D.shouldBeWithinCube(
    start: Double,
    end: Double,
) {
    this.x shouldBeGreaterThanOrEqual start
    this.y shouldBeGreaterThanOrEqual start
    this.x shouldBeLessThan end
    this.y shouldBeLessThan end
}

fun size(
    samples: List<Point2D>,
    numberOfSamples: Int,
) = stringSpec {
    "size should be $numberOfSamples" {
        samples shouldHaveSize numberOfSamples
    }
}

fun unitCube(samples: List<Point2D>) =
    stringSpec {
        "points should be within unit cube" {
            for (p in samples) {
                p.shouldBeWithinCube(0.0, 1.0)
            }
        }
    }

data class DistributionParams(
    val factorX: Double = 10.0,
    val factorY: Double = 10.0,
    val sizeOfX: Int = factorX.toInt(),
    val sizeOfY: Int = factorY.toInt(),
    val sizeOfXY: Int = sizeOfX * sizeOfY,
)

fun distribution(
    samples: List<Point2D>,
    params: DistributionParams,
) = stringSpec {
    "distribution of values" {
        val histX = Histogram()
        val histY = Histogram()
        val histXY = Histogram()

        for (p in samples) {
            val x = (p.x * params.factorX).toInt()
            val y = (p.y * params.factorY).toInt()
            histX.add(x)
            histY.add(y)
            histXY.add(x * 10 + y)
        }

        histX.keys() shouldHaveSize params.sizeOfX
        histY.keys() shouldHaveSize params.sizeOfY
        histXY.keys() shouldHaveSize params.sizeOfXY
    }
}
