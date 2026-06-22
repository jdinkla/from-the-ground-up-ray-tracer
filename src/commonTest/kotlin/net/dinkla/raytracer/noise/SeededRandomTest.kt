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
    })
