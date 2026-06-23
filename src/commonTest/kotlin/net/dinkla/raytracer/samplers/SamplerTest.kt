package net.dinkla.raytracer.samplers

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.doubles.shouldBeGreaterThanOrEqual
import io.kotest.matchers.doubles.shouldBeLessThan
import net.dinkla.raytracer.math.Point2D
import kotlin.math.sqrt

class SamplerTest :
    StringSpec({

        val NUM = 1000
        val s = Sampler(PureRandom, 100, 10)

        "testSampleUnitSquare" {
            repeat(NUM) {
                val p = s.sampleUnitSquare()
                p.x shouldBeGreaterThanOrEqual 0.0
                p.x shouldBeLessThan 1.0
                p.y shouldBeGreaterThanOrEqual 0.0
                p.y shouldBeLessThan 1.0
            }
        }

        "testSampleUnitDisk" {
            s.mapSamplesToUnitDisk()
            repeat(NUM) {
                val p = s.sampleUnitDisk()
                p.x shouldBeGreaterThanOrEqual -1.0
                p.x shouldBeLessThan 1.0
                p.y shouldBeGreaterThanOrEqual -1.0
                p.y shouldBeLessThan 1.0
                p.length shouldBeLessThan sqrt(2.0)
            }
        }

        "testSampleHemisphere" {
            s.mapSamplesToHemiSphere(1.0)
            repeat(NUM) {
                val p = s.sampleHemisphere()
                p.x shouldBeGreaterThanOrEqual -1.0
                p.x shouldBeLessThan 1.0
                p.y shouldBeGreaterThanOrEqual -1.0
                p.y shouldBeLessThan 1.0
                p.z shouldBeGreaterThanOrEqual 0.0
                p.z shouldBeLessThan 1.0
            }
        }

        "testSampleSphere" {
            s.mapSamplesToSphere()
            repeat(NUM) {
                val p = s.sampleSphere()
                p.x shouldBeGreaterThanOrEqual -1.0
                p.x shouldBeLessThan 1.0
                p.y shouldBeGreaterThanOrEqual -1.0
                p.y shouldBeLessThan 1.0
                p.z shouldBeGreaterThanOrEqual -1.0
                p.z shouldBeLessThan 1.0
            }
        }

        // shuffleXCoordinates only permutes the x-components among points; the set of x-values is a
        // permutation of the input (no value invented or lost) and every y-component is untouched.
        "shuffleXCoordinates permutes x-values and preserves all y-values" {
            val numSamples = 4
            val numSets = 3
            val points =
                MutableList(numSamples * numSets) { i ->
                    Point2D(i.toDouble(), i + 100.0)
                }
            val originalXs = points.map { it.x }
            val originalYs = points.map { it.y }

            Sampler.shuffleXCoordinates(numSamples, numSets, points)

            points.map { it.x } shouldContainExactlyInAnyOrder originalXs
            points.map { it.y } shouldContainExactlyInAnyOrder originalYs
        }

        // shuffleYCoordinates is the symmetric operation: it permutes the y-components and preserves
        // the multiset of x-components.
        "shuffleYCoordinates permutes y-values and preserves all x-values" {
            val numSamples = 4
            val numSets = 3
            val points =
                MutableList(numSamples * numSets) { i ->
                    Point2D(i.toDouble(), i + 100.0)
                }
            val originalXs = points.map { it.x }
            val originalYs = points.map { it.y }

            Sampler.shuffleYCoordinates(numSamples, numSets, points)

            points.map { it.x } shouldContainExactlyInAnyOrder originalXs
            points.map { it.y } shouldContainExactlyInAnyOrder originalYs
        }
    })
