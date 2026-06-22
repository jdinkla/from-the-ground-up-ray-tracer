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

class PartAnnulusTest : StringSpec({
    // A flat ring at the origin facing +y, radii 1..2, restricted to azimuth phi in [0, PI/2]
    // (phi = atan2(x, z): phi = 0 is +z, phi = PI/2 is +x).
    val annulus =
        PartAnnulus(
            Point3D.ORIGIN,
            innerRadius = 1.0,
            outerRadius = 2.0,
            normal = Normal.UP,
            phiMin = 0.0,
            phiMax = PI / 2.0,
        )

    "part annulus hit in the band and wedge records t and the facing normal" {
        // From (1.5, 3, 0.001) straight down: lands at (1.5, 0, 0.001), |p| = 1.5 in [1,2], phi ~ PI/2, kept.
        val ray = Ray(Point3D(1.5, 3.0, 0.001), Vector3D(0.0, -1.0, 0.0))
        val sr = Hit(Double.MAX_VALUE)

        annulus.hit(ray, sr) shouldBe true
        sr.t shouldBeApprox 3.0
        sr.normal shouldBe Normal.UP
    }

    "part annulus rejects a point inside the central hole" {
        // From (0.5, 3, 0.001) down: |p| = 0.5 < innerRadius, so the hole is missed.
        val ray = Ray(Point3D(0.5, 3.0, 0.001), Vector3D(0.0, -1.0, 0.0))

        annulus.hit(ray, Hit(Double.MAX_VALUE)) shouldBe false
    }

    "part annulus rejects a point in the band but outside the kept wedge" {
        // From (-1.5, 3, -1.5) down: |p| = 2.12... actually radius sqrt(4.5) > 2; use (-1.0, 3, -1.0):
        // |p| = sqrt(2) in [1,2] but phi = atan2(-1,-1) = 5PI/4, outside [0, PI/2].
        val ray = Ray(Point3D(-1.0, 3.0, -1.0), Vector3D(0.0, -1.0, 0.0))

        annulus.hit(ray, Hit(Double.MAX_VALUE)) shouldBe false
    }

    "part annulus shadowHit mirrors hit inside the band and wedge" {
        val ray = Ray(Point3D(1.5, 3.0, 0.001), Vector3D(0.0, -1.0, 0.0))

        val shadow = annulus.shadowHit(ray)

        shadow.shouldBeInstanceOf<Shadow.Hit>()
        shadow.t shouldBeApprox 3.0
    }

    "part annulus shadowHit returns none outside the wedge" {
        val ray = Ray(Point3D(-1.0, 3.0, -1.0), Vector3D(0.0, -1.0, 0.0))

        annulus.shadowHit(ray) shouldBe Shadow.None
    }

    "part annulus bounding box spans the outer radius" {
        val bbox = annulus.boundingBox

        bbox.p shouldBe Point3D(-2.0, -2.0, -2.0)
        bbox.q shouldBe Point3D(2.0, 2.0, 2.0)
    }
})
