package net.dinkla.raytracer.math

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import net.dinkla.raytracer.math.Axis.X
import net.dinkla.raytracer.math.Axis.Y
import net.dinkla.raytracer.math.Axis.Z

internal class AxisTest :
    StringSpec({
        "next" {
            X.next() shouldBe Y
            Y.next() shouldBe Z
            Z.next() shouldBe X
        }

        "fromInt" {
            Axis.fromInt(0) shouldBe X
            Axis.fromInt(1) shouldBe Y
            Axis.fromInt(2) shouldBe Z
            Axis.fromInt(3) shouldBe X
        }

        // A negative input gives a negative remainder in Kotlin (-1 % 3 == -1), which matches none of
        // the 0/1/2 cases and so exercises the defensive `else -> Z` branch.
        "fromInt falls back to Z for a negative index" {
            Axis.fromInt(-1) shouldBe Z
        }

        "next on each axis cycles X -> Y -> Z -> X" {
            X.next() shouldBe Y
            Y.next() shouldBe Z
            Z.next() shouldBe X
        }

        "value exposes the ordinal index of each axis" {
            X.value shouldBe 0
            Y.value shouldBe 1
            Z.value shouldBe 2
        }
    })
