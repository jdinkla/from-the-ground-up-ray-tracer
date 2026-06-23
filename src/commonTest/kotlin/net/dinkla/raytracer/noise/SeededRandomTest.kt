package net.dinkla.raytracer.noise

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.doubles.shouldBeGreaterThanOrEqual
import io.kotest.matchers.doubles.shouldBeLessThan
import io.kotest.matchers.shouldBe
import net.dinkla.raytracer.shouldBeApprox
import kotlin.math.sqrt

/**
 * The deterministic generator that seeds the noise tables. Determinism is the property the whole noise
 * system relies on (TASK-18.3 AC#4): the same seed must reproduce the same sequence, and every draw
 * must respect its documented range.
 */
class SeededRandomTest :
    StringSpec({

        "the same seed reproduces the same unit-double sequence" {
            val a = SeededRandom(7)
            val b = SeededRandom(7)

            repeat(100) {
                a.nextUnit() shouldBe b.nextUnit()
            }
        }

        "different seeds generally produce different first draws" {
            val a = SeededRandom(1).nextUnit()
            val b = SeededRandom(2).nextUnit()

            (a == b) shouldBe false
        }

        "nextUnit stays in [0, 1)" {
            val rng = SeededRandom(99)

            repeat(1000) {
                val v = rng.nextUnit()
                v shouldBeGreaterThanOrEqual 0.0
                v shouldBeLessThan 1.0
            }
        }

        "nextSignedUnit stays in [-1, 1)" {
            val rng = SeededRandom(123)

            repeat(1000) {
                val v = rng.nextSignedUnit()
                v shouldBeGreaterThanOrEqual -1.0
                v shouldBeLessThan 1.0
            }
        }

        "nextUnitVector returns a unit-length vector" {
            val rng = SeededRandom(555)

            repeat(1000) {
                val v = rng.nextUnitVector()
                val length = sqrt(v.x * v.x + v.y * v.y + v.z * v.z)
                length shouldBeApprox 1.0
            }
        }

        "the same seed reproduces the same vector sequence" {
            val a = SeededRandom(31)
            val b = SeededRandom(31)

            repeat(50) {
                a.nextUnitVector() shouldBe b.nextUnitVector()
            }
        }

        "the first draw from a fixed seed is the documented LCG value" {
            // Pins the exact LCG output so a change to the multiplier/increment/shift constants — which
            // would silently alter every render's noise — fails loudly. Derived by hand from the
            // Numerical-Recipes constants: state0 = 0*1664525 + 1013904223 = 1013904223;
            // (state0 ushr 8) = 3960563; 3960563 / 2^24 = 0.23606795072555542.
            val first = SeededRandom(0).nextUnit()

            first shouldBeApprox 0.23606795072555542
        }

        "nextSignedUnit is the unit draw stretched to [-1, 1)" {
            // Same seed, same first draw: signed = unit*2 - 1. Pins the relationship between the two.
            val unit = SeededRandom(0).nextUnit()
            val signed = SeededRandom(0).nextSignedUnit()

            signed shouldBeApprox (unit * 2.0 - 1.0)
        }

        "successive draws from one generator advance the sequence" {
            // The state mutates between calls, so consecutive draws differ (the LCG does not stall).
            val rng = SeededRandom(2024)

            val a = rng.nextUnit()
            val b = rng.nextUnit()

            (a == b) shouldBe false
        }
    })
