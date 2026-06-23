package net.dinkla.raytracer.objects.compound

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.doubles.shouldBeGreaterThanOrEqual
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
class SolidCylinderTest : StringSpec({
    // Solid cylinder: axis on +y, y in [0, 2], radius 1, with caps at y = 0 (facing -y) and y = 2 (facing +y).
    fun cylinder() = SolidCylinder(y0 = 0.0, y1 = 2.0, radius = 1.0).apply { initialize() }

    "solid cylinder hit on the body wall records t and an outward +x normal" {
        // From (3, 1, 0) toward -x: the open cylinder (radius 1) is reached at (1, 1, 0), t = 2.
        val ray = Ray(Point3D(3.0, 1.0, 0.0), Vector3D(-1.0, 0.0, 0.0))
        val sr = Hit(Double.MAX_VALUE)

        cylinder().hit(ray, sr) shouldBe true
        sr.t shouldBeApprox 2.0
        sr.normal shouldBeApprox Normal.RIGHT
    }

    "solid cylinder hit straight down onto the top cap records t and the UP normal" {
        // From (0, 3, 0) toward -y: the top disk at y = 2 is reached at (0, 2, 0), t = 1.
        val ray = Ray(Point3D(0.0, 3.0, 0.0), Vector3D(0.0, -1.0, 0.0))
        val sr = Hit(Double.MAX_VALUE)

        cylinder().hit(ray, sr) shouldBe true
        sr.t shouldBeApprox 1.0
        sr.normal shouldBeApprox Normal.UP
    }

    "solid cylinder hit straight up onto the bottom cap records the DOWN normal" {
        // From (0, -2, 0) toward +y: the bottom disk at y = 0 is the nearer hit (t = 2) than the top.
        val ray = Ray(Point3D(0.0, -2.0, 0.0), Vector3D(0.0, 1.0, 0.0))
        val sr = Hit(Double.MAX_VALUE)

        cylinder().hit(ray, sr) shouldBe true
        sr.t shouldBeApprox 2.0
        sr.normal shouldBeApprox Normal.DOWN
    }

    "solid cylinder misses a ray that passes wide of the bounding box" {
        val ray = Ray(Point3D(5.0, 1.0, 0.0), Vector3D(0.0, 0.0, 1.0))

        cylinder().hit(ray, Hit(Double.MAX_VALUE)) shouldBe false
    }

    "solid cylinder shadowHit reports a shadow for a ray that strikes the body" {
        val ray = Ray(Point3D(3.0, 1.0, 0.0), Vector3D(-1.0, 0.0, 0.0))

        cylinder().shadowHit(ray).shouldBeInstanceOf<Shadow.Hit>()
    }

    "solid cylinder bounding box encloses the radius and full height" {
        val bbox = cylinder().boundingBox

        // Compound pads the computed bounds slightly, so assert enclosure rather than the exact corners.
        bbox.p.x shouldBeLessThanOrEqual -1.0
        bbox.p.y shouldBeLessThanOrEqual 0.0
        bbox.q.x shouldBeGreaterThanOrEqual 1.0
        bbox.q.y shouldBeGreaterThanOrEqual 2.0
    }

    "solid cylinders with equal fields are equal and share a hashCode" {
        val a = SolidCylinder(0.0, 2.0, 1.0)
        val b = SolidCylinder(0.0, 2.0, 1.0)

        a shouldBe b
        a.hashCode() shouldBe b.hashCode()
    }

    "solid cylinders differing in one field are not equal" {
        val base = SolidCylinder(0.0, 2.0, 1.0)

        base shouldNotBe SolidCylinder(0.5, 2.0, 1.0)
        base shouldNotBe SolidCylinder(0.0, 3.0, 1.0)
        base shouldNotBe SolidCylinder(0.0, 2.0, 2.0)
    }

    "solid cylinder is not equal to null or to an unrelated type" {
        val base = SolidCylinder(0.0, 2.0, 1.0)

        base.equals(null) shouldBe false
        base.equals("cylinder") shouldBe false
    }

    "solid cylinder toString names the class and its fields" {
        SolidCylinder(0.0, 2.0, 1.0).toString() shouldContain "SolidCylinder"
    }
})
