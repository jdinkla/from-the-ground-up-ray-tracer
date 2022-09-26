package net.dinkla.raytracer.hits

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.objects.Triangle
import net.dinkla.raytracer.objects.compound.SolidCylinder

class HitsTest : StringSpec({
    "not compound should be accepted" {
        val p = Point3D.ORIGIN
        val hit = Hit()
        val notAcompound = Triangle(p, p, p)
        hit.geometricObject = notAcompound
        hit.geometricObject shouldBe notAcompound
    }

    "compound should be accepted" {
        val hit = Hit()
        val compound = SolidCylinder(1.0, 2.0, 3.0)
        hit.geometricObject = compound
        hit.geometricObject shouldBe compound
    }
})