package net.dinkla.raytracer.samplers

import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.doubles.shouldBeGreaterThanOrEqual
import io.kotest.matchers.doubles.shouldBeLessThan
import net.dinkla.raytracer.shouldBeApprox
import kotlin.math.sqrt

class SamplerTest : AnnotationSpec() {

    private val NUM = 1000
    private var s = Sampler(PureRandom, 100, 10)

    @Test
    fun testSampleUnitSquare() {
        (1..NUM).forEach {
            val p = s.sampleUnitSquare()
            p.x shouldBeGreaterThanOrEqual 0.0
            p.x shouldBeLessThan 1.0
            p.y shouldBeGreaterThanOrEqual 0.0
            p.y shouldBeLessThan 1.0
        }
    }

    @Test
    fun testSampleUnitDisk() {
        s.mapSamplesToUnitDisk()
        (1..NUM).forEach {
            val p = s.sampleUnitDisk()
            p.x shouldBeGreaterThanOrEqual -1.0
            p.x shouldBeLessThan 1.0
            p.y shouldBeGreaterThanOrEqual -1.0
            p.y shouldBeLessThan 1.0
            p.length() shouldBeLessThan sqrt(2.0)
        }
    }

    @Test
    fun testSampleHemisphere() {
        s.mapSamplesToHemiSphere(1.0)
        (1..NUM).forEach {
            val p = s.sampleHemisphere()
            p.x shouldBeGreaterThanOrEqual -1.0
            p.x shouldBeLessThan 1.0
            p.y shouldBeGreaterThanOrEqual -1.0
            p.y shouldBeLessThan 1.0
            p.z shouldBeGreaterThanOrEqual 0.0
            p.z shouldBeLessThan 1.0
            p.length() shouldBeApprox 1.0
        }
    }

    @Test
    fun testSampleSphere() {
        s.mapSamplesToSphere()
        (1..NUM).forEach {
            val p = s.sampleSphere()
            p.x shouldBeGreaterThanOrEqual -1.0
            p.x shouldBeLessThan 1.0
            p.y shouldBeGreaterThanOrEqual -1.0
            p.y shouldBeLessThan 1.0
            p.z shouldBeGreaterThanOrEqual -1.0
            p.z shouldBeLessThan 1.0
            p.length() shouldBeApprox 1.0
        }
    }
}
