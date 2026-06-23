package net.dinkla.raytracer.objects

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.types.shouldBeInstanceOf
import net.dinkla.raytracer.hits.Hit
import net.dinkla.raytracer.hits.Shadow
import net.dinkla.raytracer.math.Face
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.math.Vector3D
import net.dinkla.raytracer.shouldBeApprox

internal class AlignedBoxTest :
    StringSpec({

        val p = Point3D.ORIGIN
        val q = Point3D.UNIT

        "hit" {
            val ab = AlignedBox(p, q)
            val ray = Ray(Point3D(0.5, 0.5, -1.0), Vector3D.FORWARD)
            val sr = Hit()
            val hit = ab.hit(ray, sr)
            hit shouldBe true
            sr.t shouldBe 1.0
        }

        "shadowHit" {
            val ab = AlignedBox(p, q)
            val ray = Ray(Point3D(0.5, 0.5, -1.0), Vector3D.FORWARD)
            val sr = Hit()
            val hit = ab.shadowHit(ray, sr)
            hit shouldBe true
            sr.t shouldBe 1.0
        }

        // A ray entering through the FRONT face (along +z) makes tzMin the largest entering t,
        // exercising the `tzMin > t0` branch and selecting faceIn = FRONT (c >= 0).
        "hit entering through the front face reports the front normal" {
            val ab = AlignedBox(p, q)
            val ray = Ray(Point3D(0.5, 0.5, -1.0), Vector3D.FORWARD)
            val sr = Hit()

            ab.hit(ray, sr) shouldBe true
            sr.t shouldBeApprox 1.0
            sr.normal shouldBe Face.FRONT.normal
        }

        // A ray along +x enters through the LEFT face: txMin dominates the entering test (a >= 0).
        "hit entering through the left face reports the left normal" {
            val ab = AlignedBox(p, q)
            val ray = Ray(Point3D(-1.0, 0.5, 0.5), Vector3D.RIGHT)
            val sr = Hit()

            ab.hit(ray, sr) shouldBe true
            sr.t shouldBeApprox 1.0
            sr.normal shouldBe Face.LEFT.normal
        }

        // A ray along +y enters through the BOTTOM face: the tyMin branch (b >= 0) wins.
        "hit entering through the bottom face reports the bottom normal" {
            val ab = AlignedBox(p, q)
            val ray = Ray(Point3D(0.5, -1.0, 0.5), Vector3D.UP)
            val sr = Hit()

            ab.hit(ray, sr) shouldBe true
            sr.t shouldBeApprox 1.0
            sr.normal shouldBe Face.BOTTOM.normal
        }

        // Origin inside the box: t0 <= epsilon, so the exit face/distance branch is taken.
        "hit from inside the box reports the exit face and distance" {
            val ab = AlignedBox(p, q)
            val ray = Ray(Point3D(0.5, 0.5, 0.5), Vector3D.FORWARD)
            val sr = Hit()

            ab.hit(ray, sr) shouldBe true
            sr.t shouldBeApprox 0.5 // exits the far +z face at z = 1
            sr.normal shouldBe Face.BACK.normal // exiting face for a +z ray
        }

        // A ray travelling in -x enters through the RIGHT face (a = 1/dir.x < 0, so the `a >= 0` false
        // branches are taken for both the entering face (RIGHT) and the exiting face (LEFT)).
        "hit travelling in negative x enters through the right face" {
            val ab = AlignedBox(p, q)
            val ray = Ray(Point3D(2.0, 0.5, 0.5), Vector3D(-1.0, 0.0, 0.0))
            val sr = Hit()

            ab.hit(ray, sr) shouldBe true
            sr.t shouldBeApprox 1.0 // enters the x = 1 plane after 1 unit
            sr.normal shouldBe Face.RIGHT.normal
        }

        // A ray travelling in -y enters through the TOP face (b = 1/dir.y < 0, so the `b >= 0` false
        // branches are taken for both the entering face (TOP) and the exiting face (BOTTOM)).
        "hit travelling in negative y enters through the top face" {
            val ab = AlignedBox(p, q)
            val ray = Ray(Point3D(0.5, 2.0, 0.5), Vector3D(0.0, -1.0, 0.0))
            val sr = Hit()

            ab.hit(ray, sr) shouldBe true
            sr.t shouldBeApprox 1.0 // enters the y = 1 plane after 1 unit
            sr.normal shouldBe Face.TOP.normal
        }

        // A +x shadow ray makes txMin the largest entering t and txMax the smallest exiting t, taking
        // the `txMin > tyMin` true and `txMax < tyMax` true branches of shadowHit (and `tzMin > t0`
        // false), which the existing +z shadow rays never exercise.
        "shadowHit travelling in positive x enters via the x slab" {
            val ab = AlignedBox(p, q)
            val ray = Ray(Point3D(-1.0, 0.5, 0.5), Vector3D.RIGHT)

            val shadow = ab.shadowHit(ray)

            shadow.shouldBeInstanceOf<Shadow.Hit>()
            shadow.t shouldBeApprox 1.0
        }

        "miss when the ray points away from the box" {
            val ab = AlignedBox(p, q)
            val ray = Ray(Point3D(0.5, 0.5, -1.0), Vector3D(0.0, 0.0, -1.0))

            ab.hit(ray, Hit()) shouldBe false
            ab.shadowHit(ray) shouldBe Shadow.None
        }

        "miss when the ray passes wide of the box" {
            val ab = AlignedBox(p, q)
            val ray = Ray(Point3D(5.0, 5.0, -1.0), Vector3D.FORWARD)

            ab.hit(ray, Hit()) shouldBe false
            ab.shadowHit(ray) shouldBe Shadow.None
        }

        "shadowHit returns a Hit with the entering distance" {
            val ab = AlignedBox(p, q)
            val ray = Ray(Point3D(0.5, 0.5, -1.0), Vector3D.FORWARD)

            val shadow = ab.shadowHit(ray)

            shadow.shouldBeInstanceOf<Shadow.Hit>()
            shadow.t shouldBeApprox 1.0
        }

        "shadowHit from inside the box returns the exit distance" {
            val ab = AlignedBox(p, q)
            val ray = Ray(Point3D(0.5, 0.5, 0.5), Vector3D.FORWARD)

            val shadow = ab.shadowHit(ray)

            shadow.shouldBeInstanceOf<Shadow.Hit>()
            shadow.t shouldBeApprox 0.5
        }

        "equal boxes are equal and share a hash code" {
            val a1 = AlignedBox(p, q)
            val a2 = AlignedBox(Point3D.ORIGIN, Point3D.UNIT)

            a1 shouldBe a2
            a1.hashCode() shouldBe a2.hashCode()
        }

        "boxes differing in a corner are not equal" {
            AlignedBox(p, q) shouldNotBe AlignedBox(p, Point3D(2.0, 2.0, 2.0))
        }

        "a box is not equal to a non-box value" {
            AlignedBox(p, q).equals("x") shouldBe false
        }

        "toString names the class" {
            AlignedBox(p, q).toString() shouldContain "AlignedBox"
        }
    })
