package net.dinkla.raytracer.samplers

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe

class ConstantTest : StringSpec({
    val numSamples = 10
    val numSets = 10
    val numberOfSamples = numSamples * numSets

    val samples = Constant(0.2, 0.8).generateSamples(numSamples, numSets)

    include(size(samples, numberOfSamples))
    include(unitCube(samples, numberOfSamples))
    include(distribution(samples, DistributionParams(10.0, 10.0, 1, 1, 1)))

    "generated values should be (0.2, 0.3)" {
        val sample = Constant(0.2, 0.3).generateSamples(2, 3)
        sample shouldHaveSize 2 * 3
        for (s in sample) {
            s.x shouldBe 0.2
            s.y shouldBe 0.3
        }
    }
})
