package net.dinkla.raytracer.lights

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.colors.Color.Companion.WHITE
import net.dinkla.raytracer.math.Point3D

class PointLightTest : StringSpec({
    "construct a point light with defaults for ls and color" {
        val location = Point3D(1.0, 2.0, 3.0)
        val p = PointLight(location)
        p.ls shouldBe 1.0
        p.color shouldBe WHITE
        p.location shouldBe location
    }

    "construct a point light without defaults" {
        val location = Point3D(1.0, 2.0, 3.0)
        val ls = 0.12
        val color = Color(0.12, 0.23, 0.34)
        val p = PointLight(location, ls, color)
        p.ls shouldBe ls
        p.color shouldBe color
        p.location shouldBe location
    }
})
