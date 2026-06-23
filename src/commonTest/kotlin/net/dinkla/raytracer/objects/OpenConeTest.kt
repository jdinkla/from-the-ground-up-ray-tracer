package net.dinkla.raytracer.objects

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.doubles.shouldBeGreaterThan
import io.kotest.matchers.doubles.shouldBeLessThan
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.types.shouldBeInstanceOf
import net.dinkla.raytracer.hits.Hit
import net.dinkla.raytracer.hits.Shadow
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.math.Vector3D
import net.dinkla.raytracer.shouldBeApprox

class OpenConeTest : StringSpec({
    // Cone: base radius 1 at y = 0, apex at (0, 2, 0). Surface: x^2 + z^2 = 0.25 * (2 - y)^2.
    val cone = OpenCone(height = 2.0, radius = 1.0)

    "open cone hit records t and an outward-and-upward normal" {
        // At y = 1 the surface radius is 0.5; ray from (2,1,0) toward -x first hits (0.5, 1, 0).
        val ray = Ray(Point3D(2.0, 1.0, 0.0), Vector3D(-1.0, 0.0, 0.0))
        val sr = Hit(Double.MAX_VALUE)

        cone.hit(ray, sr) shouldBe true
        sr.t shouldBeApprox 1.5 // hit x = 0.5, travelled 2 - 0.5 = 1.5
        sr.normal.x shouldBeGreaterThan 0.0 // points outward (+x side)
        sr.normal.y shouldBeGreaterThan 0.0 // and upward, since the surface slopes toward the apex
        sr.normal.z shouldBeApprox 0.0
    }

    "open cone rejects intersections outside the y extent" {
        // The infinite cone is met at y = 3 (above the apex); the finite cone must reject it.
        val ray = Ray(Point3D(0.0, 3.0, -5.0), Vector3D(0.0, 0.0, 1.0))

        cone.hit(ray, Hit(Double.MAX_VALUE)) shouldBe false
        cone.shadowHit(ray) shouldBe Shadow.None
    }

    "open cone misses when the ray passes wide of the surface" {
        val ray = Ray(Point3D(5.0, 1.0, 0.0), Vector3D(0.0, 0.0, 1.0))

        cone.hit(ray, Hit(Double.MAX_VALUE)) shouldBe false
        cone.shadowHit(ray) shouldBe Shadow.None
    }

    "open cone shadowHit mirrors hit" {
        val ray = Ray(Point3D(2.0, 1.0, 0.0), Vector3D(-1.0, 0.0, 0.0))

        val shadow = cone.shadowHit(ray)

        shadow.shouldBeInstanceOf<Shadow.Hit>()
        shadow.t shouldBeApprox 1.5
    }

    "open cone bounding box contains base and apex" {
        val bbox = cone.boundingBox

        bbox.p.x shouldBeApprox -1.0
        bbox.p.y shouldBe 0.0
        bbox.q.x shouldBeApprox 1.0
        bbox.q.y shouldBeApprox 2.0
    }

    // A ray from inside the cone hitting the far wall: the near root is behind the origin, so the
    // far root (t2) is accepted and the normal is flipped to face the incoming ray.
    "open cone hit from inside takes the far root with an inward-facing normal" {
        // Inside the cone at y = 1 (surface radius 0.5) heading +x toward the far wall at x = 0.5.
        val ray = Ray(Point3D(0.0, 1.0, 0.0), Vector3D(1.0, 0.0, 0.0))
        val sr = Hit(Double.MAX_VALUE)

        cone.hit(ray, sr) shouldBe true
        sr.t shouldBeApprox 0.5 // far wall at x = 0.5
        sr.normal.x shouldBeLessThan 0.0 // flipped to face the incoming +x ray
    }

    "open cone returns no hit when the quadratic degenerates (a == 0)" {
        // direction (1, 2, 0): dx^2 + dz^2 = 1 == k * dy^2 = 0.25 * 4, so the leading coefficient a is
        // zero and roots() returns null. Origin placed wide so there is genuinely nothing to hit.
        val ray = Ray(Point3D(5.0, 0.0, 5.0), Vector3D(1.0, 2.0, 0.0))

        cone.hit(ray, Hit(Double.MAX_VALUE)) shouldBe false
        cone.shadowHit(ray) shouldBe Shadow.None
    }

    "equal cones are equal and share a hash code" {
        val c1 = OpenCone(height = 2.0, radius = 1.0)
        val c2 = OpenCone(height = 2.0, radius = 1.0)

        c1 shouldBe c2
        c1.hashCode() shouldBe c2.hashCode()
    }

    "cones differing in height are not equal" {
        OpenCone(height = 2.0, radius = 1.0) shouldNotBe OpenCone(height = 3.0, radius = 1.0)
    }

    "a cone is not equal to a non-cone value" {
        OpenCone(height = 2.0, radius = 1.0).equals("x") shouldBe false
    }

    "toString names the class" {
        OpenCone(height = 2.0, radius = 1.0).toString() shouldContain "OpenCone"
    }
})
