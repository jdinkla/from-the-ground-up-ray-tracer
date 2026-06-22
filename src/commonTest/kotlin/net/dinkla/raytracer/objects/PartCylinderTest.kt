package net.dinkla.raytracer.objects

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import net.dinkla.raytracer.hits.Hit
import net.dinkla.raytracer.hits.Shadow
import net.dinkla.raytracer.math.MathUtils.PI
import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.math.Vector3D
import net.dinkla.raytracer.shouldBeApprox

class PartCylinderTest : StringSpec({
    // Open cylinder radius 1, y in [-1, 1], restricted to azimuth phi in [0, PI] (phi = atan2(x, z)).
    val cylinder = PartCylinder(-1.0, 1.0, 1.0, phiMin = 0.0, phiMax = PI)

    "part cylinder hit on the kept wedge records t and outward normal" {
        // Hits at (1, 0, 0): phi = atan2(1, 0) = PI/2, inside [0, PI].
        val ray = Ray(Point3D(2.0, 0.0, 0.0), Vector3D(-1.0, 0.0, 0.0))
        val sr = Hit(Double.MAX_VALUE)

        cylinder.hit(ray, sr) shouldBe true
        sr.t shouldBeApprox 1.0
        sr.normal shouldBeApprox Normal.RIGHT
    }

    "part cylinder rejects a ray hitting only the cut-away azimuth" {
        // Both intersections at x = -0.5 (phi outside [0, PI]); a full cylinder would hit.
        val ray = Ray(Point3D(-0.5, 0.0, 2.0), Vector3D(0.0, 0.0, -1.0))

        cylinder.hit(ray, Hit(Double.MAX_VALUE)) shouldBe false
        cylinder.shadowHit(ray) shouldBe Shadow.None
    }

    "part cylinder rejects a ray outside the y extent" {
        // y = 2 is above the cylinder's top at y = 1.
        val ray = Ray(Point3D(0.5, 2.0, -2.0), Vector3D(0.0, 0.0, 1.0))

        cylinder.hit(ray, Hit(Double.MAX_VALUE)) shouldBe false
        cylinder.shadowHit(ray) shouldBe Shadow.None
    }

    "part cylinder misses when the ray misses the cylinder entirely" {
        val ray = Ray(Point3D(2.5, 0.0, 0.0), Vector3D(1.0, 0.0, 0.0))

        cylinder.hit(ray, Hit(Double.MAX_VALUE)) shouldBe false
    }

    "part cylinder shadowHit mirrors hit on the kept wedge" {
        val ray = Ray(Point3D(2.0, 0.0, 0.0), Vector3D(-1.0, 0.0, 0.0))

        val shadow = cylinder.shadowHit(ray)

        shadow.shouldBeInstanceOf<Shadow.Hit>()
        shadow.t shouldBeApprox 1.0
    }

    "part cylinder bounding box contains the cylinder" {
        val bbox = cylinder.boundingBox

        bbox.p.x shouldBeApprox -1.0
        bbox.p.y shouldBe -1.0
        bbox.q.x shouldBeApprox 1.0
        bbox.q.y shouldBe 1.0
    }
})
