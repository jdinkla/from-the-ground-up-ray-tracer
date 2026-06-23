package net.dinkla.raytracer.objects

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.types.shouldBeInstanceOf
import net.dinkla.raytracer.hits.Hit
import net.dinkla.raytracer.hits.Shadow
import net.dinkla.raytracer.math.MathUtils.PI
import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.math.Vector3D
import net.dinkla.raytracer.shouldBeApprox

@Suppress("EqualsNullCall")
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

    "concave part sphere skips a nearer hit in the cut-away wedge and accepts the farther kept one" {
        // From (0,3,0) toward -y: the near root is the top (0,2,0) at t=1 (theta=0, cut away);
        // the far root is the bottom (0,-2,0) at t=5 (theta=PI, kept). So t1 is rejected, t2 accepted.
        val ray = Ray(Point3D(0.0, 3.0, 0.0), Vector3D(0.0, -1.0, 0.0))
        val sr = Hit(Double.MAX_VALUE)

        lowerHemisphere.hit(ray, sr) shouldBe true
        sr.t shouldBeApprox 5.0
        sr.normal shouldBeApprox Normal.UP
    }

    "concave part sphere rejects a ray whose both intersections lie in the cut-away wedge" {
        // A horizontal ray at y = 1.5 (> 0) crosses the sphere twice, both points in the
        // upper hemisphere (theta < PI/2) that this lower-hemisphere object cuts away.
        val ray = Ray(Point3D(-3.0, 1.5, 0.0), Vector3D(1.0, 0.0, 0.0))

        lowerHemisphere.hit(ray, Hit(Double.MAX_VALUE)) shouldBe false
        lowerHemisphere.shadowHit(ray) shouldBe Shadow.None
    }

    "concave part sphere rejects a ray whose intersections all lie behind the origin" {
        // From (0,5,0) toward +y: the sphere is entirely behind, both roots are negative.
        val ray = Ray(Point3D(0.0, 5.0, 0.0), Vector3D(0.0, 1.0, 0.0))

        lowerHemisphere.hit(ray, Hit(Double.MAX_VALUE)) shouldBe false
        lowerHemisphere.shadowHit(ray) shouldBe Shadow.None
    }

    "concave part sphere shadowHit skips the cut-away near root and reports the farther kept one" {
        val ray = Ray(Point3D(0.0, 3.0, 0.0), Vector3D(0.0, -1.0, 0.0))

        val shadow = lowerHemisphere.shadowHit(ray)

        shadow.shouldBeInstanceOf<Shadow.Hit>()
        shadow.t shouldBeApprox 5.0
    }

    "concave part sphere rejects an azimuth wedge cut by phi" {
        // Wedge phi in [0, PI/2] (atan2(x,z): 0 is +z, PI/2 is +x). A downward ray at z < 0
        // lands at phi ~ PI (the -z half) which is outside [0, PI/2].
        val quarter = ConcavePartSphere(Point3D.ORIGIN, 2.0, phiMin = 0.0, phiMax = PI / 2.0)
        val ray = Ray(Point3D(0.0, 3.0, -1.0), Vector3D(0.0, -1.0, 0.0))

        quarter.hit(ray, Hit(Double.MAX_VALUE)) shouldBe false
    }

    "concave part sphere bounding box contains the full sphere" {
        val bbox = lowerHemisphere.boundingBox

        bbox.p shouldBe Point3D(-2.0, -2.0, -2.0)
        bbox.q shouldBe Point3D(2.0, 2.0, 2.0)
    }

    "concave part spheres with equal fields are equal and share a hashCode" {
        val a = ConcavePartSphere(Point3D.ORIGIN, 2.0, thetaMin = PI / 2.0, thetaMax = PI)
        val b = ConcavePartSphere(Point3D.ORIGIN, 2.0, thetaMin = PI / 2.0, thetaMax = PI)

        a shouldBe b
        a.hashCode() shouldBe b.hashCode()
    }

    "concave part spheres differing in one field are not equal" {
        val base = ConcavePartSphere(Point3D.ORIGIN, 2.0, thetaMin = PI / 2.0, thetaMax = PI)

        base shouldNotBe ConcavePartSphere(Point3D(1.0, 0.0, 0.0), 2.0, thetaMin = PI / 2.0, thetaMax = PI)
        base shouldNotBe ConcavePartSphere(Point3D.ORIGIN, 3.0, thetaMin = PI / 2.0, thetaMax = PI)
        base shouldNotBe ConcavePartSphere(Point3D.ORIGIN, 2.0, phiMin = 0.1, thetaMin = PI / 2.0, thetaMax = PI)
        base shouldNotBe ConcavePartSphere(Point3D.ORIGIN, 2.0, phiMax = PI, thetaMin = PI / 2.0, thetaMax = PI)
        base shouldNotBe ConcavePartSphere(Point3D.ORIGIN, 2.0, thetaMin = 0.0, thetaMax = PI)
        base shouldNotBe ConcavePartSphere(Point3D.ORIGIN, 2.0, thetaMin = PI / 2.0, thetaMax = PI / 2.0)
    }

    "concave part sphere is not equal to null or to an unrelated type" {
        val base = ConcavePartSphere(Point3D.ORIGIN, 2.0, thetaMin = PI / 2.0, thetaMax = PI)

        base.equals(null) shouldBe false
        base.equals("sphere") shouldBe false
    }

    "concave part sphere toString contains the class name" {
        ConcavePartSphere(Point3D.ORIGIN, 2.0).toString() shouldContain "ConcavePartSphere"
    }
})
