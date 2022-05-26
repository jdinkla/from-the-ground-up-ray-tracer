package net.dinkla.raytracer.math

import io.kotest.core.spec.style.AnnotationSpec
import net.dinkla.raytracer.shouldBeApprox

class AffineTransformationTest : AnnotationSpec() {

    internal var p = Point3D(1.0, 2.0, 3.0)

    @Test
    fun testTranslate() {
        val t = AffineTransformation()
        t.translate(Vector3D(2.0, 3.0, 4.0))
        t.shouldSatisfy(p, Point3D(-1.0, -1.0, -1.0), Point3D(3.0, 5.0, 7.0))
    }

    @Test
    fun testScale() {
        val t = AffineTransformation()
        t.scale(Vector3D(1.0, 2.0, 3.0))
        t.shouldSatisfy(p, Point3D(1.0, 1.0, 1.0), Point3D(1.0, 4.0, 9.0))
    }

    @Test
    fun testRotateX() {
        val t = AffineTransformation()
        t.rotate(Axis.X,90.0)
        t.shouldSatisfy(Point3D(1.0, 1.0, 1.0), Point3D(1.0, 1.0, -1.0), Point3D(1.0, -1.0, 1.0))
    }

    @Test
    fun testRotateY() {
        val t = AffineTransformation()
        t.rotate(Axis.Y,90.0)
        t.shouldSatisfy(Point3D(1.0, 1.0, 1.0), Point3D(-1.0, 1.0, 1.0), Point3D(1.0, 1.0, -1.0))
    }

    @Test
    fun testRotateZ() {
        val t = AffineTransformation()
        t.rotate(Axis.Z, 90.0)
        t.shouldSatisfy(Point3D(1.0, 1.0, 1.0), Point3D(1.0, -1.0, 1.0), Point3D(-1.0, 1.0, 1.0))
    }
}

fun AffineTransformation.shouldSatisfy(p: Point3D, inverse: Point3D, forward: Point3D) {
    invMatrix.times(p) shouldBeApprox inverse
    forwardMatrix.times(p) shouldBeApprox forward
    // composition yields identity
    invMatrix.times(forwardMatrix.times(p)) shouldBeApprox p
    forwardMatrix.times(invMatrix.times(p)) shouldBeApprox p
}



