package net.dinkla.raytracer.objects.arealights

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import net.dinkla.raytracer.hits.Shade
import net.dinkla.raytracer.materials.Emissive
import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Vector3D
import net.dinkla.raytracer.samplers.Sampler
import net.dinkla.raytracer.shouldBeApprox

class RectangleLightTest : StringSpec({
    val edgeA = Vector3D(2.0, 0.0, 0.0)
    val edgeB = Vector3D(0.0, 3.0, 0.0)

    fun rectangleLight() =
        RectangleLight(
            sampler = Sampler(),
            p0 = Point3D.ORIGIN,
            a = edgeA,
            b = edgeB,
            normal = Normal.FORWARD,
        )

    "rectangle light pdf is the reciprocal of the rectangle area" {
        // pdf = 1 / (|a| * |b|) = 1 / (2 * 3) = 1/6.
        rectangleLight().pdf(Shade()) shouldBeApprox 1.0 / 6.0
    }

    "rectangle light reports its construction normal everywhere" {
        val light = rectangleLight()

        light.getNormal(Point3D(1.0, 1.0, 0.0)) shouldBe Normal.FORWARD
    }

    "rectangle light derives its normal from a x b when none is given" {
        // The secondary constructor sets normal = (a x b).normalize(); for +x cross +y that is +z.
        val light = RectangleLight(Sampler(), Point3D.ORIGIN, edgeA, edgeB)

        light.getNormal(Point3D.ORIGIN) shouldBe Normal.FORWARD
    }

    "rectangle light getLightMaterial returns the assigned material" {
        val material = Emissive()
        val light = rectangleLight().apply { this.material = material }

        light.getLightMaterial() shouldBe material
    }

    "rectangle light getLightMaterial throws when no material was assigned" {
        val light = rectangleLight()

        shouldThrow<IllegalArgumentException> { light.getLightMaterial() }
    }
})
