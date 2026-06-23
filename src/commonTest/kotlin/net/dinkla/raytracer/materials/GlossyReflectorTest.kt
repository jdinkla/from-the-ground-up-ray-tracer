package net.dinkla.raytracer.materials

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain
import net.dinkla.raytracer.colors.Color

/**
 * Covers [GlossyReflector]'s value-type contract ([equals]/[hashCode]/[toString]). The glossy
 * reflection recursion and its no-tracer branch are already pinned by [GlossyReflectorShadeTest].
 */
internal class GlossyReflectorTest :
    StringSpec({

        fun glossy(): GlossyReflector =
            GlossyReflector().apply {
                exp = 100.0
                kr = 0.6
                cr = Color(0.2, 0.4, 0.9)
            }

        "two GlossyReflectors with the same parameters are equal and share a hash code" {
            val a = glossy()
            val b = glossy()

            a shouldBe b
            a.hashCode() shouldBe b.hashCode()
        }

        "GlossyReflectors differing in the reflection coefficient are not equal" {
            val a = glossy()
            val b = glossy().apply { kr = 0.7 }

            a shouldNotBe b
        }

        "GlossyReflectors differing in the reflection colour are not equal" {
            val a = glossy()
            val b = glossy().apply { cr = Color(0.3, 0.4, 0.9) }

            a shouldNotBe b
        }

        // Differing in a base Phong property (here the diffuse/ambient colour) makes super.equals
        // false, so the && short-circuits before comparing the glossy BRDF (equals L92's left-false
        // branch). Both still carry identical glossy BRDFs, isolating the base-class difference.
        "GlossyReflectors differing only in the base Phong colour are not equal" {
            val a = GlossyReflector(Color.RED).apply { exp = 100.0 }
            val b = GlossyReflector(Color.BLUE).apply { exp = 100.0 }

            a shouldNotBe b
        }

        "a GlossyReflector is not equal to null or to a non-GlossyReflector value" {
            val a = glossy()

            a shouldNotBe null
            (a.equals("not a material")) shouldBe false
        }

        "toString names the material" {
            glossy().toString() shouldContain "GlossyReflector"
        }
    })
