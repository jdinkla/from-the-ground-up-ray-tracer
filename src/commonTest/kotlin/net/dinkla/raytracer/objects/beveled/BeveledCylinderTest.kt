package net.dinkla.raytracer.objects.beveled

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.doubles.shouldBeGreaterThan
import io.kotest.matchers.doubles.shouldBeLessThanOrEqual
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.types.shouldBeInstanceOf
import net.dinkla.raytracer.hits.Hit
import net.dinkla.raytracer.hits.Shadow
import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.math.Vector3D
import net.dinkla.raytracer.shouldBeApprox

@Suppress("EqualsNullCall")
class BeveledCylinderTest : StringSpec({
    // A solid cylinder, y in [0, 2], radius 1, edge bevel radius 0.2.
    // Body wall at radius 1 over y in [0.2, 1.8]; caps of radius 0.8 at y = 0 and y = 2;
    // torus rims (sweep radius 0.8, tube radius 0.2) centred at y = 0.2 and y = 1.8.
    fun cylinder() = BeveledCylinder(y0 = 0.0, y1 = 2.0, radius = 1.0, rb = 0.2).apply { initialize() }

    "beveled cylinder hit on the BODY wall records t and an outward +x normal" {
        // From (3, 1, 0) toward -x: the body cylinder (radius 1) is reached at (1, 1, 0), t = 2.
        val ray = Ray(Point3D(3.0, 1.0, 0.0), Vector3D(-1.0, 0.0, 0.0))
        val sr = Hit(Double.MAX_VALUE)

        cylinder().hit(ray, sr) shouldBe true
        sr.t shouldBeApprox 2.0
        sr.normal shouldBeApprox Normal.RIGHT
    }

    "beveled cylinder hit straight down onto the top CAP records t and the UP normal" {
        // From (0, 3, 0) toward -y: the top cap (radius 0.8) at y = 2 is reached at (0, 2, 0), t = 1.
        val ray = Ray(Point3D(0.0, 3.0, 0.0), Vector3D(0.0, -1.0, 0.0))
        val sr = Hit(Double.MAX_VALUE)

        cylinder().hit(ray, sr) shouldBe true
        sr.t shouldBeApprox 1.0
        sr.normal shouldBeApprox Normal.UP
    }

    "beveled cylinder hit on the rounded top RIM lands on the torus apex" {
        // From (0.8, 3, 0) toward -y: x = 0.8 is the rim's sweep radius, so the ray meets the top of the
        // torus tube at y = 1.8 + 0.2 = 2.0, t = 1. This is the case the torus phantom-root fix unblocks.
        val ray = Ray(Point3D(0.8, 3.0, 0.0), Vector3D(0.0, -1.0, 0.0))
        val sr = Hit(Double.MAX_VALUE)

        cylinder().hit(ray, sr) shouldBe true
        sr.t shouldBeApprox 1.0
        sr.normal.y shouldBeGreaterThan 0.0 // the rim apex faces +y
    }

    "beveled cylinder misses a ray aimed away from it" {
        // From (3, 1, 0) toward +x: travels away from the cylinder.
        val ray = Ray(Point3D(3.0, 1.0, 0.0), Vector3D(1.0, 0.0, 0.0))

        cylinder().hit(ray, Hit(Double.MAX_VALUE)) shouldBe false
    }

    "beveled cylinder shadowHit reports a shadow for a ray that strikes the body" {
        val ray = Ray(Point3D(3.0, 1.0, 0.0), Vector3D(-1.0, 0.0, 0.0))

        val shadow = cylinder().shadowHit(ray)

        shadow.shouldBeInstanceOf<Shadow.Hit>()
    }

    "beveled cylinder bounding box contains the full cylinder extent" {
        val bbox = cylinder().boundingBox

        // The bbox must enclose the radius-1, y in [0,2] cylinder (the Instance-wrapped rims make it a
        // conservative over-estimate, never smaller).
        bbox.p.x shouldBeLessThanOrEqual -1.0
        bbox.p.y shouldBeLessThanOrEqual 0.0
        bbox.p.z shouldBeLessThanOrEqual -1.0
        bbox.q.x shouldBeGreaterThan 1.0 - 1e-9
        bbox.q.y shouldBeGreaterThan 2.0 - 1e-9
        bbox.q.z shouldBeGreaterThan 1.0 - 1e-9
    }

    "beveled cylinders with equal fields are equal and share a hashCode" {
        val a = BeveledCylinder(0.0, 2.0, 1.0, 0.2)
        val b = BeveledCylinder(0.0, 2.0, 1.0, 0.2)

        a shouldBe b
        a.hashCode() shouldBe b.hashCode()
    }

    "beveled cylinders differing in one field are not equal" {
        val base = BeveledCylinder(0.0, 2.0, 1.0, 0.2)

        base shouldNotBe BeveledCylinder(0.5, 2.0, 1.0, 0.2)
        base shouldNotBe BeveledCylinder(0.0, 3.0, 1.0, 0.2)
        base shouldNotBe BeveledCylinder(0.0, 2.0, 2.0, 0.2)
        base shouldNotBe BeveledCylinder(0.0, 2.0, 1.0, 0.3)
    }

    "beveled cylinder is not equal to null or to an unrelated type" {
        val base = BeveledCylinder(0.0, 2.0, 1.0, 0.2)

        base.equals(null) shouldBe false
        base.equals("cylinder") shouldBe false
    }

    "beveled cylinder toString names the class" {
        BeveledCylinder(0.0, 2.0, 1.0, 0.2).toString() shouldContain "BeveledCylinder"
    }
})
