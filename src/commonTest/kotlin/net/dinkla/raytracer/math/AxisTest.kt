package net.dinkla.raytracer.math

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import net.dinkla.raytracer.math.Axis.X
import net.dinkla.raytracer.math.Axis.Y
import net.dinkla.raytracer.math.Axis.Z

internal class AxisTest : StringSpec({
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
})
