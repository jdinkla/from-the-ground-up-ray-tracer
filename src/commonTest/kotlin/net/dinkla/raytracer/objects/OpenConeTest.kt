package net.dinkla.raytracer.objects

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.doubles.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
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
})
