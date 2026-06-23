package net.dinkla.raytracer.samplers

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldHaveSize
import net.dinkla.raytracer.samplers.IGenerator.Companion.sqrt

class MultiJitteredTest :
    StringSpec({
        val numSamples = 1000
        val numSets = 10
        // n = sqrt(numSamples); each set holds exactly n*n points, so the total is numSets * n*n.
        // (Before TASK-31 the generator allocated one extra origin slot, numSets*n*n + 1.)
        val numberOfSamples = numSets * sqrt(numSamples) * sqrt(numSamples)

        val samples = MultiJittered.generateSamples(numSamples, numSets)

        include(size(samples, numberOfSamples))
        include(unitCube(samples))
        include(distribution(samples, DistributionParams(10.0, 10.0)))

        // A second, small configuration exercises the inner loop bounds at different sizes so the
        // shuffle-index branches (Random.int(j, n)) are hit for both small and large n.
        "a small configuration produces the expected sample count" {
            val small = MultiJittered.generateSamples(4, 2)
            // generateSamples allocates numSets * n * n slots (n = sqrt(4) = 2).
            small shouldHaveSize 2 * 2 * 2
        }

        "every sample of the small configuration lies in the unit square" {
            val small = MultiJittered.generateSamples(4, 2)
            for (p in small) {
                p.shouldBeWithinCube(0.0, 1.0)
            }
        }
    })
