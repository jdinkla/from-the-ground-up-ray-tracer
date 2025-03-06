package net.dinkla.raytracer.samplers

import io.kotest.core.spec.style.StringSpec
import net.dinkla.raytracer.samplers.IGenerator.Companion.sqrt

class RegularTest :
    StringSpec({
        val numSamples = 1000
        val numSets = 10
        val numberOfSamples = numSets * sqrt(numSamples) * sqrt(numSamples)

        val samples = Regular.generateSamples(numSamples, numSets)

        include(size(samples, numberOfSamples))
        include(unitCube(samples))
        include(distribution(samples, DistributionParams(10.0, 10.0)))
    })
