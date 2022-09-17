package net.dinkla.raytracer.samplers

import io.kotest.core.spec.style.StringSpec

class HammersleyTest : StringSpec({
    val numSamples = 1000
    val numSets = 10
    val numberOfSamples = numSamples * numSets

    val samples = Hammersley.generateSamples(numSamples, numSets)

    include(size(samples, numberOfSamples))
    include(unitCube(samples, numberOfSamples))
    include(distribution(samples, DistributionParams(10.0, 10.0)))
})
