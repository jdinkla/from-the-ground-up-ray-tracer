package net.dinkla.raytracer.samplers

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.doubles.shouldBeGreaterThanOrEqual
import io.kotest.matchers.doubles.shouldBeLessThan

/**
 * Regression coverage for TASK-31: [Sampler.sampleUnitSquare] used to index its point array assuming
 * exactly `numSamples * numSets` generated points, but the sqrt-based generators
 * ([MultiJittered]/[Jittered]/[Regular]) only generate `floor(sqrt(numSamples))^2 * numSets` points.
 * That mismatch threw [IndexOutOfBoundsException] for non-square `numSamples`, and [MultiJittered]
 * additionally threw whenever `numSets > sqrt(numSamples)` (its per-set stride used `p * numSets`
 * instead of `p * n * n`).
 *
 * These tests pin the contract: for every generator, across non-square sample counts and set counts
 * larger than `sqrt(numSamples)`, drawing many samples must yield valid points in `[0, 1)^2` and
 * never throw.
 */
class SamplerIndexingTest :
    StringSpec({

        // Each generator must be usable from Sampler for arbitrary (numSamples, numSets).
        val generators: List<Pair<String, IGenerator>> =
            listOf(
                "MultiJittered" to MultiJittered,
                "Jittered" to Jittered,
                "Regular" to Regular,
                "NRooks" to NRooks,
                "PureRandom" to PureRandom,
            )

        // Non-square counts plus set counts that exceed sqrt(numSamples) — exactly the regime that
        // used to throw. 20 -> sqrt = 4, so numSets 11 > 4; 50 -> sqrt = 7, so numSets 13 > 7.
        val configs: List<Pair<Int, Int>> =
            listOf(
                20 to 11,
                20 to 3,
                50 to 13,
                7 to 5,
                30 to 17,
            )

        // Enough draws to wrap past several sets (jump = Random.int(numSets) * stride) and exercise
        // the index arithmetic that used to overflow.
        val draws = 2000

        for ((name, generator) in generators) {
            for ((numSamples, numSets) in configs) {
                "sampleUnitSquare of $name stays in the unit square for numSamples=$numSamples numSets=$numSets" {
                    val sampler = Sampler(generator, numSamples, numSets)

                    repeat(draws) {
                        val p = sampler.sampleUnitSquare()
                        p.x shouldBeGreaterThanOrEqual 0.0
                        p.x shouldBeLessThan 1.0
                        p.y shouldBeGreaterThanOrEqual 0.0
                        p.y shouldBeLessThan 1.0
                    }
                }

                "sampleUnitDisk of $name stays in the unit disk for numSamples=$numSamples numSets=$numSets" {
                    val sampler = Sampler(generator, numSamples, numSets)
                    sampler.mapSamplesToUnitDisk()

                    repeat(draws) {
                        val p = sampler.sampleUnitDisk()
                        p.length shouldBeLessThan 1.001
                    }
                }

                "sampleHemisphere of $name stays on the upper hemisphere for numSamples=$numSamples numSets=$numSets" {
                    val sampler = Sampler(generator, numSamples, numSets)
                    sampler.mapSamplesToHemiSphere(1.0)

                    repeat(draws) {
                        val p = sampler.sampleHemisphere()
                        p.x shouldBeGreaterThanOrEqual -1.0
                        p.x shouldBeLessThan 1.0
                        p.y shouldBeGreaterThanOrEqual -1.0
                        p.y shouldBeLessThan 1.0
                        p.z shouldBeGreaterThanOrEqual 0.0
                        p.z shouldBeLessThan 1.0001
                    }
                }

                "sampleSphere of $name returns a point for numSamples=$numSamples numSets=$numSets" {
                    val sampler = Sampler(generator, numSamples, numSets)
                    sampler.mapSamplesToSphere()

                    repeat(draws) {
                        val p = sampler.sampleSphere()
                        p.z shouldBeGreaterThanOrEqual -1.0
                        p.z shouldBeLessThan 1.0001
                    }
                }
            }
        }
    })
