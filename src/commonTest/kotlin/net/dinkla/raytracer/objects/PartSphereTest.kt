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

class PartSphereTest : StringSpec({
    // Unit sphere at the origin keeping only the azimuth wedge phi in [0, PI]
    // (phi = atan2(x, z)), so the +x facing surface is kept and the -x facing surface is cut away.
    val halfByPhi = PartSphere(Point3D.ORIGIN, 1.0, phiMin = 0.0, phiMax = PI)

    // Unit sphere keeping only the upper polar cap theta in [0, PI/2] (theta = acos(y)), i.e. y >= 0.
    val upperCap = PartSphere(Point3D.ORIGIN, 1.0, thetaMin = 0.0, thetaMax = PI / 2.0)

    "part sphere hit on the kept hemisphere records t and outward normal" {
        // Hits the sphere at (1, 0, 0): phi = atan2(1, 0) = PI/2, inside [0, PI].
        val ray = Ray(Point3D(2.0, 0.0, 0.0), Vector3D(-1.0, 0.0, 0.0))
        val sr = Hit(Double.MAX_VALUE)

        halfByPhi.hit(ray, sr) shouldBe true
        sr.t shouldBeApprox 1.0
        sr.normal shouldBeApprox Normal.RIGHT
    }

    "part sphere rejects a ray that hits the full sphere but only on the cut-away azimuth" {
        // Both intersection points lie at x = -0.5 (phi outside [0, PI]); a full sphere would hit.
        val ray = Ray(Point3D(-0.5, 0.0, -2.0), Vector3D(0.0, 0.0, 1.0))

        halfByPhi.hit(ray, Hit(Double.MAX_VALUE)) shouldBe false
        halfByPhi.shadowHit(ray) shouldBe Shadow.None
    }

    "part sphere rejects a ray confined to the cut-away polar band" {
        // Both intersections sit at y = -0.5 (theta = acos(-0.5) = 120deg), outside [0, 90deg].
        val ray = Ray(Point3D(0.0, -0.5, -2.0), Vector3D(0.0, 0.0, 1.0))

        upperCap.hit(ray, Hit(Double.MAX_VALUE)) shouldBe false
        upperCap.shadowHit(ray) shouldBe Shadow.None
    }

    "part sphere misses when the ray misses the sphere entirely" {
        val ray = Ray(Point3D(2.0, 2.0, 0.0), Vector3D(0.0, 1.0, 0.0))

        halfByPhi.hit(ray, Hit(Double.MAX_VALUE)) shouldBe false
    }

    "part sphere shadowHit mirrors hit on the kept hemisphere" {
        val ray = Ray(Point3D(2.0, 0.0, 0.0), Vector3D(-1.0, 0.0, 0.0))

        val shadow = halfByPhi.shadowHit(ray)

        shadow.shouldBeInstanceOf<Shadow.Hit>()
        shadow.t shouldBeApprox 1.0
    }

    "part sphere bounding box contains the full sphere" {
        val bbox = halfByPhi.boundingBox

        bbox.p shouldBe Point3D(-1.0, -1.0, -1.0)
        bbox.q shouldBe Point3D(1.0, 1.0, 1.0)
    }
})
