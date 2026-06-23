package net.dinkla.raytracer.objects

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.types.shouldBeInstanceOf
import net.dinkla.raytracer.hits.Hit
import net.dinkla.raytracer.hits.Shadow
import net.dinkla.raytracer.math.BBox
import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.math.Vector3D
import net.dinkla.raytracer.shouldBeApprox

internal class RectangleTest :
    StringSpec({

        val p0 = Point3D.ORIGIN
        val a = Vector3D(1.1, 0.0, 0.0)
        val b = Vector3D(0.0, 2.2, 0.0)

        // A unit square in the z = 0 plane spanning x in [0,1], y in [0,1], facing +z.
        val unitA = Vector3D.RIGHT
        val unitB = Vector3D.UP
        val unitRect = Rectangle(p0, unitA, unitB)

        "should calculate bounding box and normal" {
            val r = Rectangle(p0, a, b)
            r.boundingBox shouldBe BBox(Point3D.ORIGIN, Point3D(1.1, 2.2, 0.0))
            r.normal shouldBe Normal.create((a cross b).normalize())
        }

        "should calculate bounding box and normal when inverted" {
            val r = Rectangle(p0, a, b, true)
            r.boundingBox shouldBe BBox(Point3D.ORIGIN, Point3D(1.1, 2.2, 0.0))
            r.normal shouldBe Normal.create((b cross a).normalize())
        }

        "the explicit-normal constructor keeps the supplied normal" {
            val n = Normal(0.0, 0.0, 1.0)

            val r = Rectangle(p0, a, b, n)

            r.normal shouldBe n
            r.boundingBox shouldBe BBox(Point3D.ORIGIN, Point3D(1.1, 2.2, 0.0))
        }

        "hit through the interior records t and the rectangle normal" {
            val ray = Ray(Point3D(0.25, 0.25, -1.0), Vector3D.FORWARD)
            val sr = Hit(Double.MAX_VALUE)

            unitRect.hit(ray, sr) shouldBe true
            sr.t shouldBeApprox 1.0
            sr.normal shouldBe unitRect.normal
        }

        "miss when the intersection is behind the ray origin (t <= epsilon)" {
            // Origin sits on the rectangle plane, so t = 0 <= epsilon.
            val ray = Ray(Point3D(0.25, 0.25, 0.0), Vector3D.FORWARD)

            unitRect.hit(ray, Hit(Double.MAX_VALUE)) shouldBe false
            unitRect.shadowHit(ray) shouldBe Shadow.None
        }

        "miss when the hit point falls left of the a edge (ddota < 0)" {
            val ray = Ray(Point3D(-0.5, 0.25, -1.0), Vector3D.FORWARD)

            unitRect.hit(ray, Hit(Double.MAX_VALUE)) shouldBe false
            unitRect.shadowHit(ray) shouldBe Shadow.None
        }

        "miss when the hit point falls beyond the a edge (ddota > sqrLength)" {
            val ray = Ray(Point3D(1.5, 0.25, -1.0), Vector3D.FORWARD)

            unitRect.hit(ray, Hit(Double.MAX_VALUE)) shouldBe false
            unitRect.shadowHit(ray) shouldBe Shadow.None
        }

        "miss when the hit point falls below the b edge (ddotb < 0)" {
            val ray = Ray(Point3D(0.25, -0.5, -1.0), Vector3D.FORWARD)

            unitRect.hit(ray, Hit(Double.MAX_VALUE)) shouldBe false
            unitRect.shadowHit(ray) shouldBe Shadow.None
        }

        "miss when the hit point falls beyond the b edge (ddotb > sqrLength)" {
            val ray = Ray(Point3D(0.25, 1.5, -1.0), Vector3D.FORWARD)

            unitRect.hit(ray, Hit(Double.MAX_VALUE)) shouldBe false
            unitRect.shadowHit(ray) shouldBe Shadow.None
        }

        "shadowHit through the interior returns a Hit with the distance" {
            val ray = Ray(Point3D(0.25, 0.25, -1.0), Vector3D.FORWARD)

            val shadow = unitRect.shadowHit(ray)

            shadow.shouldBeInstanceOf<Shadow.Hit>()
            shadow.t shouldBeApprox 1.0
        }

        "equal rectangles are equal and share a hash code" {
            val r1 = Rectangle(p0, a, b)
            val r2 = Rectangle(Point3D.ORIGIN, Vector3D(1.1, 0.0, 0.0), Vector3D(0.0, 2.2, 0.0))

            r1 shouldBe r2
            r1.hashCode() shouldBe r2.hashCode()
        }

        "rectangles differing in a side are not equal" {
            Rectangle(p0, a, b) shouldNotBe Rectangle(p0, Vector3D(3.0, 0.0, 0.0), b)
        }

        // The remaining clauses of the equals && chain: differing only in p0 (first clause), in the
        // b side (third clause), or in the normal (fourth clause, via the explicit-normal constructor).
        "rectangles differing in the corner point are not equal" {
            Rectangle(p0, a, b) shouldNotBe Rectangle(Point3D(1.0, 0.0, 0.0), a, b)
        }

        "rectangles differing in the b side are not equal" {
            Rectangle(p0, a, b) shouldNotBe Rectangle(p0, a, Vector3D(0.0, 3.0, 0.0))
        }

        "rectangles with the same sides but different normals are not equal" {
            val n1 = Normal(0.0, 0.0, 1.0)
            val n2 = Normal(0.0, 0.0, -1.0)

            Rectangle(p0, a, b, n1) shouldNotBe Rectangle(p0, a, b, n2)
        }

        "a rectangle is not equal to a non-rectangle value" {
            Rectangle(p0, a, b).equals("x") shouldBe false
        }

        "toString names the class" {
            Rectangle(p0, a, b).toString() shouldContain "Rectangle"
        }
    })
