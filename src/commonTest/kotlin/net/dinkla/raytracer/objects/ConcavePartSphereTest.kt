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

class ConcavePartSphereTest : StringSpec({
    // Lower hemisphere (theta in [PI/2, PI], i.e. y <= 0) of a radius-2 sphere, with inward normals.
    val lowerHemisphere = ConcavePartSphere(Point3D.ORIGIN, 2.0, thetaMin = PI / 2.0, thetaMax = PI)

    "concave part sphere hit on the kept hemisphere records t and an INWARD normal" {
        // From (0,-0.5,0) toward -y: the bottom is reached at (0,-2,0), t = 1.5, theta = PI in range.
        // The outward normal there is -y; the inward (concave) normal is +y.
        val ray = Ray(Point3D(0.0, -0.5, 0.0), Vector3D(0.0, -1.0, 0.0))
        val sr = Hit(Double.MAX_VALUE)

        lowerHemisphere.hit(ray, sr) shouldBe true
        sr.t shouldBeApprox 1.5
        sr.normal shouldBeApprox Normal.UP
    }

    "concave part sphere rejects a ray confined to the cut-away upper hemisphere" {
        // From (0, 0.5, 0) toward +y: the only forward hit is the top (0,2,0), theta = 0, outside [PI/2, PI].
        val ray = Ray(Point3D(0.0, 0.5, 0.0), Vector3D(0.0, 1.0, 0.0))

        lowerHemisphere.hit(ray, Hit(Double.MAX_VALUE)) shouldBe false
        lowerHemisphere.shadowHit(ray) shouldBe Shadow.None
    }

    "concave part sphere misses when the ray misses the sphere entirely" {
        val ray = Ray(Point3D(3.0, -3.0, 0.0), Vector3D(0.0, 0.0, 1.0))

        lowerHemisphere.hit(ray, Hit(Double.MAX_VALUE)) shouldBe false
    }

    "concave part sphere shadowHit mirrors hit on the kept hemisphere" {
        val ray = Ray(Point3D(0.0, -0.5, 0.0), Vector3D(0.0, -1.0, 0.0))

        val shadow = lowerHemisphere.shadowHit(ray)

        shadow.shouldBeInstanceOf<Shadow.Hit>()
        shadow.t shouldBeApprox 1.5
    }

    "concave part sphere bounding box contains the full sphere" {
        val bbox = lowerHemisphere.boundingBox

        bbox.p shouldBe Point3D(-2.0, -2.0, -2.0)
        bbox.q shouldBe Point3D(2.0, 2.0, 2.0)
    }
})
