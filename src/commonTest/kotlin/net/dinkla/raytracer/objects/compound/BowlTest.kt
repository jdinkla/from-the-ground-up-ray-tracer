package net.dinkla.raytracer.objects.compound

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import net.dinkla.raytracer.hits.Hit
import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.math.Vector3D
import net.dinkla.raytracer.shouldBeApprox

class BowlTest : StringSpec({
    // Bowl: thick hemispherical shell, inner radius 1, outer radius 2, opening upward (lower half).
    val bowl = Bowl(innerRadius = 1.0, outerRadius = 2.0)

    "bowl hit on the OUTER wall from below records t and an outward DOWN normal" {
        // From (0,-3,0) toward +y: the outer sphere's bottom is reached at (0,-2,0), t = 1.
        // The convex outer wall's outward normal there points -y.
        val ray = Ray(Point3D(0.0, -3.0, 0.0), Vector3D(0.0, 1.0, 0.0))
        val sr = Hit(Double.MAX_VALUE)

        bowl.hit(ray, sr) shouldBe true
        sr.t shouldBeApprox 1.0
        sr.normal shouldBeApprox Normal.DOWN
    }

    "bowl hit on the INNER wall reports the inward (concave) normal" {
        // From (0,-0.5,0) toward -y inside the cavity: the inner sphere is reached at (0,-1,0), t = 0.5.
        // The concave inner wall's normal points inward (toward the centre), i.e. +y.
        val ray = Ray(Point3D(0.0, -0.5, 0.0), Vector3D(0.0, -1.0, 0.0))
        val sr = Hit(Double.MAX_VALUE)

        bowl.hit(ray, sr) shouldBe true
        sr.t shouldBeApprox 0.5
        sr.normal shouldBeApprox Normal.UP
    }

    "bowl hit straight down onto the rim records the UP normal" {
        // From (1.5, 1, 0) toward -y: the rim annulus at y = 0 is reached at (1.5, 0, 0), t = 1.
        // |p| = 1.5 lies in the band [1, 2].
        val ray = Ray(Point3D(1.5, 1.0, 0.0), Vector3D(0.0, -1.0, 0.0))
        val sr = Hit(Double.MAX_VALUE)

        bowl.hit(ray, sr) shouldBe true
        sr.t shouldBeApprox 1.0
        sr.normal shouldBeApprox Normal.UP
    }

    "bowl misses a ray that passes wide of the bounding box" {
        val ray = Ray(Point3D(5.0, 1.0, 0.0), Vector3D(0.0, 0.0, 1.0))

        bowl.hit(ray, Hit(Double.MAX_VALUE)) shouldBe false
    }

    "bowl bounding box spans the outer radius and only the lower half" {
        val bbox = bowl.boundingBox

        bbox.p.x shouldBeApprox -2.0
        bbox.p.y shouldBeApprox -2.0
        bbox.q.x shouldBeApprox 2.0
        bbox.q.y shouldBeApprox 0.0
    }
})
