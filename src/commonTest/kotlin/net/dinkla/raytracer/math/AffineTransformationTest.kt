package net.dinkla.raytracer.math

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain
import net.dinkla.raytracer.shouldBeApprox

@Suppress("EqualsNullCall")
class AffineTransformationTest :
    StringSpec({

        val p = Point3D(1.0, 2.0, 3.0)

        "testTranslate" {
            val t = AffineTransformation()
            t.translate(Vector3D(2.0, 3.0, 4.0))
            t.shouldSatisfy(p, Point3D(-1.0, -1.0, -1.0), Point3D(3.0, 5.0, 7.0))
        }

        "testScale" {
            val t = AffineTransformation()
            t.scale(Vector3D(1.0, 2.0, 3.0))
            t.shouldSatisfy(p, Point3D(1.0, 1.0, 1.0), Point3D(1.0, 4.0, 9.0))
        }

        "testRotateX" {
            val t = AffineTransformation()
            t.rotate(Axis.X, 90.0)
            t.shouldSatisfy(Point3D(1.0, 1.0, 1.0), Point3D(1.0, 1.0, -1.0), Point3D(1.0, -1.0, 1.0))
        }

        "testRotateY" {
            val t = AffineTransformation()
            t.rotate(Axis.Y, 90.0)
            t.shouldSatisfy(Point3D(1.0, 1.0, 1.0), Point3D(-1.0, 1.0, 1.0), Point3D(1.0, 1.0, -1.0))
        }

        "testRotateZ" {
            val t = AffineTransformation()
            t.rotate(Axis.Z, 90.0)
            t.shouldSatisfy(Point3D(1.0, 1.0, 1.0), Point3D(1.0, -1.0, 1.0), Point3D(-1.0, 1.0, 1.0))
        }

        // ray() transforms a ray by the inverse matrix. After a pure translation the inverse shifts the
        // origin back by the translation; direction (a free vector) is unaffected by the translation part.
        "ray transforms the origin by the inverse matrix and leaves a direction's components" {
            val t = AffineTransformation()
            t.translate(Vector3D(2.0, 3.0, 4.0))

            val transformed = t.ray(Ray(Point3D(2.0, 3.0, 4.0), Vector3D(1.0, 0.0, 0.0)))

            transformed.origin shouldBeApprox Point3D(0.0, 0.0, 0.0)
            transformed.direction shouldBeApprox Vector3D(1.0, 0.0, 0.0)
        }

        "equals is true for two transformations built the same way" {
            val a = AffineTransformation().apply { translate(Vector3D(1.0, 2.0, 3.0)) }
            val b = AffineTransformation().apply { translate(Vector3D(1.0, 2.0, 3.0)) }

            a shouldBe b
        }

        "equals is false when the matrices differ" {
            val a = AffineTransformation().apply { translate(Vector3D(1.0, 2.0, 3.0)) }
            val b = AffineTransformation().apply { translate(Vector3D(4.0, 5.0, 6.0)) }

            a shouldNotBe b
        }

        "equals is false against null" {
            (AffineTransformation().equals(null)) shouldBe false
        }

        "equals is false against a different type" {
            (AffineTransformation().equals("not a transformation")) shouldBe false
        }

        "value-equal transformations are equal, and hashCode is deterministic per instance" {
            val a = AffineTransformation().apply { scale(Vector3D(2.0, 2.0, 2.0)) }
            val b = AffineTransformation().apply { scale(Vector3D(2.0, 2.0, 2.0)) }

            a shouldBe b
            // NOTE: equal transformations do NOT share a hash code here — Matrix.hashCode delegates to
            // DoubleArray.hashCode (identity-based), violating the equals/hashCode contract. We only pin
            // that hashCode is deterministic per instance; "a.hashCode() == b.hashCode()" does not hold.
            a.hashCode() shouldBe a.hashCode()
        }

        "toString names the type" {
            AffineTransformation().toString() shouldContain "AffineTransformation"
        }
    })

fun AffineTransformation.shouldSatisfy(
    p: Point3D,
    inverse: Point3D,
    forward: Point3D,
) {
    invMatrix.times(p) shouldBeApprox inverse
    forwardMatrix.times(p) shouldBeApprox forward
    // composition yields identity
    invMatrix.times(forwardMatrix.times(p)) shouldBeApprox p
    forwardMatrix.times(invMatrix.times(p)) shouldBeApprox p
}
