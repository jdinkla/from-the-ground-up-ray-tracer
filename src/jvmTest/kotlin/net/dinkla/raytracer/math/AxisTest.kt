package net.dinkla.raytracer.math

import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.shouldBe
import net.dinkla.raytracer.math.Axis.X
import net.dinkla.raytracer.math.Axis.Y
import net.dinkla.raytracer.math.Axis.Z

internal class AxisTest : AnnotationSpec() {

    @Test
    operator fun next() {
        X.next() shouldBe Y
        Y.next() shouldBe Z
        Z.next() shouldBe X
    }

    @Test
    fun fromInt() {
        Axis.fromInt(0) shouldBe X
        Axis.fromInt(1) shouldBe Y
        Axis.fromInt(2) shouldBe Z
        Axis.fromInt(5) shouldBe Z
    }
}