package net.dinkla.raytracer.objects.arealights

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import net.dinkla.raytracer.hits.Shade
import net.dinkla.raytracer.materials.Emissive
import net.dinkla.raytracer.math.MathUtils.PI
import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.samplers.Sampler
import net.dinkla.raytracer.shouldBeApprox

class DiskLightTest : StringSpec({
    fun diskLight() =
        DiskLight(
            sampler = Sampler(),
            center = Point3D.ORIGIN,
            radius = 2.0,
            normal = Normal.UP,
        )

    "disk light pdf is the reciprocal of the disk area" {
        // pdf = 1 / (PI * r^2) with r = 2 -> 1 / (4 PI).
        diskLight().pdf(Shade()) shouldBeApprox 1.0 / (PI * 4.0)
    }

    "disk light reports its construction normal everywhere" {
        val light = diskLight()

        light.getNormal(Point3D(1.0, 0.0, 1.0)) shouldBe Normal.UP
    }

    "disk light getLightMaterial returns the assigned material" {
        val material = Emissive()
        val light = diskLight().apply { this.material = material }

        light.getLightMaterial() shouldBe material
    }

    "disk light getLightMaterial throws when no material was assigned" {
        val light = diskLight()

        shouldThrow<IllegalArgumentException> { light.getLightMaterial() }
    }
})
