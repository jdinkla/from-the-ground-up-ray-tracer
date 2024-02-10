package net.dinkla.raytracer.samplers

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.doubles.shouldBeGreaterThanOrEqual
import io.kotest.matchers.doubles.shouldBeLessThan
import kotlin.math.sqrt

class SamplerTest : StringSpec({

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
})
