package net.dinkla.raytracer.objects

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.doubles.shouldBeLessThan
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.types.shouldBeInstanceOf
import net.dinkla.raytracer.hits.Hit
import net.dinkla.raytracer.hits.Shade
import net.dinkla.raytracer.hits.Shadow
import net.dinkla.raytracer.math.MathUtils.K_EPSILON
import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.math.Normal.Companion.BACKWARD
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.math.Vector3D
import net.dinkla.raytracer.shouldBeApprox
import kotlin.math.abs

internal class SphereTest :
    StringSpec({

        val sphere = Sphere(Point3D.ORIGIN, 1.0)

        "boundingBox" {
            val bbox = sphere.boundingBox

            bbox.p shouldBe Point3D(-1.0, -1.0, -1.0)
            bbox.q shouldBe Point3D(1.0, 1.0, 1.0)
        }

        "hit" {
            val sr = Shade()
            val o = Point3D(0.0, 0.0, -2.0)
            val d = Vector3D(0.0, 0.0, 1.0)
            val ray = Ray(o, d)

            val isHit = sphere.hit(ray, sr)

            isHit shouldBe true
            abs(sr.t - 1.0) shouldBeLessThan K_EPSILON
            sr.normal shouldBe BACKWARD
        }

        "miss when the ray points away from the sphere (disc < 0)" {
            val ray = Ray(Point3D(0.0, 5.0, -2.0), Vector3D.FORWARD)

            sphere.hit(ray, Hit(Double.MAX_VALUE)) shouldBe false
            sphere.shadowHit(ray) shouldBe Shadow.None
        }

        // Origin inside the sphere: the near root is behind the origin, so the far root
        // (-b + e) / denom is taken.
        "hit from inside the sphere takes the far root" {
            val ray = Ray(Point3D.ORIGIN, Vector3D.FORWARD)
            val sr = Hit(Double.MAX_VALUE)

            sphere.hit(ray, sr) shouldBe true
            sr.t shouldBeApprox 1.0 // exits at z = 1
            sr.normal shouldBeApprox Normal.FORWARD
        }

        "shadowHit through the sphere returns a Hit with the near distance" {
            val ray = Ray(Point3D(0.0, 0.0, -2.0), Vector3D.FORWARD)

            val shadow = sphere.shadowHit(ray)

            shadow.shouldBeInstanceOf<Shadow.Hit>()
            shadow.t shouldBeApprox 1.0
        }

        "shadowHit from inside the sphere takes the far root" {
            val ray = Ray(Point3D.ORIGIN, Vector3D.FORWARD)

            val shadow = sphere.shadowHit(ray)

            shadow.shouldBeInstanceOf<Shadow.Hit>()
            shadow.t shouldBeApprox 1.0
        }

        "equal spheres are equal and share a hash code" {
            val s1 = Sphere(Point3D.ORIGIN, 1.0)
            val s2 = Sphere(Point3D(0.0, 0.0, 0.0), 1.0)

            s1 shouldBe s2
            s1.hashCode() shouldBe s2.hashCode()
        }

        "spheres differing in radius are not equal" {
            Sphere(Point3D.ORIGIN, 1.0) shouldNotBe Sphere(Point3D.ORIGIN, 2.0)
        }

        "spheres differing in centre are not equal" {
            Sphere(Point3D.ORIGIN, 1.0) shouldNotBe Sphere(Point3D(1.0, 0.0, 0.0), 1.0)
        }

        "a sphere is not equal to a non-sphere value" {
            Sphere(Point3D.ORIGIN, 1.0).equals("x") shouldBe false
        }

        "toString names the class" {
            Sphere(Point3D.ORIGIN, 1.0).toString() shouldContain "Sphere"
        }
    })
