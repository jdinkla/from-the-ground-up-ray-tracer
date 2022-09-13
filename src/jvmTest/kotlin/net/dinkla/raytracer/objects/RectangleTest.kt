package net.dinkla.raytracer.objects

import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.shouldBe
import net.dinkla.raytracer.math.BBox
import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Vector3D

internal class RectangleTest : AnnotationSpec() {

    val p0 = Point3D.ORIGIN
    val a = Vector3D(1.1, 0.0, 0.0)
    val b = Vector3D(0.0, 2.2, 0.0)

    @Test
    fun `should calculate bounding box and normal`() {
        val r = Rectangle(p0, a, b)
        r.boundingBox shouldBe BBox(Point3D.ORIGIN, Point3D(1.1, 2.2, 0.0))
        r.normal shouldBe Normal.create((a cross b).normalize())
    }

    @Test
    fun `should calculate bounding box and normal when inverted`() {
        val r = Rectangle(p0, a, b, true)
        r.boundingBox shouldBe BBox(Point3D.ORIGIN, Point3D(1.1, 2.2, 0.0))
        r.normal shouldBe Normal.create((b cross a).normalize())
    }
}