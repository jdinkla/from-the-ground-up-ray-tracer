package net.dinkla.raytracer.objects.compound

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import net.dinkla.raytracer.hits.Hit
import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.math.Vector3D
import net.dinkla.raytracer.shouldBeApprox

class ThickRingTest : StringSpec({
    // Thick ring: y in [0, 2], inner radius 1, outer radius 2.
    val ring = ThickRing(y0 = 0.0, y1 = 2.0, innerRadius = 1.0, outerRadius = 2.0)

    "thick ring hit on the OUTER wall records t and an outward +x normal" {
        // From (3,1,0) toward -x: the nearest surface is the outer cylinder at (2,1,0), t = 1.
        val ray = Ray(Point3D(3.0, 1.0, 0.0), Vector3D(-1.0, 0.0, 0.0))
        val sr = Hit(Double.MAX_VALUE)

        ring.hit(ray, sr) shouldBe true
        sr.t shouldBeApprox 1.0
        sr.normal shouldBeApprox Normal.RIGHT
    }

    "thick ring hit from the hollow centre strikes the INNER wall" {
        // From (0,1,0) toward +x through the hole: the inner cylinder is reached at (1,1,0), t = 1.
        // The inner wall faces the incoming ray, so its normal points back toward -x.
        val ray = Ray(Point3D(0.0, 1.0, 0.0), Vector3D(1.0, 0.0, 0.0))
        val sr = Hit(Double.MAX_VALUE)

        ring.hit(ray, sr) shouldBe true
        sr.t shouldBeApprox 1.0
        sr.normal shouldBeApprox Normal.LEFT
    }

    "thick ring hit straight down onto the top rim records the UP normal" {
        // From (1.5, 3, 0) toward -y: the top annulus at y = 2 is reached at (1.5, 2, 0), t = 1.
        // |p - centre| = 1.5 lies in the band [1, 2], so the rim is hit.
        val ray = Ray(Point3D(1.5, 3.0, 0.0), Vector3D(0.0, -1.0, 0.0))
        val sr = Hit(Double.MAX_VALUE)

        ring.hit(ray, sr) shouldBe true
        sr.t shouldBeApprox 1.0
        sr.normal shouldBeApprox Normal.UP
    }

    "thick ring misses a ray dropping straight through the central hole" {
        // From (0, 3, 0) down the axis: at y = 2 the point is (0,2,0), radius 0 < innerRadius, so the
        // rim is missed; the axial ray never reaches either cylinder wall.
        val ray = Ray(Point3D(0.0, 3.0, 0.0), Vector3D(0.0, -1.0, 0.0))

        ring.hit(ray, Hit(Double.MAX_VALUE)) shouldBe false
    }

    "thick ring bounding box spans the outer radius and full height" {
        val bbox = ring.boundingBox

        bbox.p.x shouldBeApprox -2.0
        bbox.p.y shouldBe 0.0
        bbox.q.x shouldBeApprox 2.0
        bbox.q.y shouldBe 2.0
    }
})
