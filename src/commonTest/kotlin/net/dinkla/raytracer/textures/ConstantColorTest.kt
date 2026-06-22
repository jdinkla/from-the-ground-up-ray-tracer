package net.dinkla.raytracer.textures

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.math.Point3D

class ConstantColorTest :
    StringSpec({
        "returns its colour regardless of the hit point" {
            val texture = ConstantColor(Color(0.2, 0.4, 0.6))

            val here = texture.getColor(testShade(Point3D.ORIGIN))
            val elsewhere = texture.getColor(testShade(Point3D(5.0, -3.0, 8.0)))

            here shouldBe Color(0.2, 0.4, 0.6)
            elsewhere shouldBe Color(0.2, 0.4, 0.6)
        }
    })
