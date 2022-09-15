package net.dinkla.raytracer.hits

import io.kotest.core.spec.style.StringSpec
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.objects.Triangle
import net.dinkla.raytracer.objects.compound.SolidCylinder
import org.junit.jupiter.api.assertThrows

class HitsTest : StringSpec( {
    "not compound should be accepted" {
        val p = Point3D.ORIGIN
        val hit = Hit()
        hit.geometricObject = Triangle(p, p, p)
    }

    "compound should not be accepted" {
        val p = Point3D.ORIGIN
        val hit = Hit()
        assertThrows<AssertionError> {
            hit.geometricObject = SolidCylinder(1.0, 2.0, 3.0)
        }
    }
})