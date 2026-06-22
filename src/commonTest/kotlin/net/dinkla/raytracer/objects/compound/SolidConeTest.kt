package net.dinkla.raytracer.objects.compound

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import net.dinkla.raytracer.hits.Hit
import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.math.Vector3D
import net.dinkla.raytracer.shouldBeApprox

class SolidConeTest : StringSpec({
    // Solid cone: base radius 1 at y = 0, apex at (0, 2, 0), with a base cap facing -y.
    val cone = SolidCone(height = 2.0, radius = 1.0)

    "solid cone hit on the lateral surface records t and an upward outward normal" {
        // Same lateral hit as the open cone: from (2,1,0) toward -x reaches (0.5, 1, 0).
        val ray = Ray(Point3D(2.0, 1.0, 0.0), Vector3D(-1.0, 0.0, 0.0))
        val sr = Hit(Double.MAX_VALUE)

        cone.hit(ray, sr) shouldBe true
        sr.t shouldBeApprox 1.5
    }

    "solid cone hit on the base cap records the downward normal" {
        // Straight up through the base disk at (0.5, 0, 0); the cap is the nearer hit (t = 2)
        // versus the lateral surface at y = 1 (t = 3).
        val ray = Ray(Point3D(0.5, -2.0, 0.0), Vector3D(0.0, 1.0, 0.0))
        val sr = Hit(Double.MAX_VALUE)

        cone.hit(ray, sr) shouldBe true
        sr.t shouldBeApprox 2.0
        sr.normal shouldBeApprox Normal.DOWN
    }

    "solid cone misses when the ray is wide of the bounding box" {
        val ray = Ray(Point3D(3.0, 1.0, 0.0), Vector3D(0.0, 0.0, 1.0))

        cone.hit(ray, Hit(Double.MAX_VALUE)) shouldBe false
    }

    "solid cone bounding box contains base and apex" {
        val bbox = cone.boundingBox

        bbox.p.x shouldBeApprox -1.0
        bbox.p.y shouldBe 0.0
        bbox.q.x shouldBeApprox 1.0
        bbox.q.y shouldBeApprox 2.0
    }
})
