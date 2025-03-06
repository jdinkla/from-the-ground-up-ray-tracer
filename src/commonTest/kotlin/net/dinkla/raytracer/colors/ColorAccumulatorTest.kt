package net.dinkla.raytracer.colors

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import net.dinkla.raytracer.colors.Color.Companion.BLACK
import net.dinkla.raytracer.colors.Color.Companion.GREEN
import net.dinkla.raytracer.colors.Color.Companion.RED

internal class ColorAccumulatorTest :
    StringSpec({

        "initial average value is black" {
            val a = ColorAccumulator()
            a.average shouldBe BLACK
        }

        "adding one changes average" {
            val a = ColorAccumulator()
            a.plus(RED)
            a.average shouldBe RED
        }

        "adding two changes average" {
            val a = ColorAccumulator()
            a.plus(RED)
            a.plus(GREEN)
            a.average shouldBe 0.5 * (RED + GREEN)
        }
    })
