package net.dinkla.raytracer.objects

import net.dinkla.raytracer.math.BBox
import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Vector3D
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class RectangleTest {

    val p0 = Point3D.ORIGIN
    val a = Vector3D(1.1, 0.0, 0.0)
    val b = Vector3D(0.0, 2.2, 0.0)

    @Test
    fun `should calculate bounding box and normal`() {
        val r = Rectangle(p0, a, b)
        assertEquals(BBox(Point3D.ORIGIN, Point3D(1.1, 2.2, 0.0)), r.boundingBox)
        assertEquals(Normal((a cross b).normalize()), r.normal)
    }

    @Test
    fun `should calculate bounding box and normal when inverted`() {
        val r = Rectangle(p0, a, b, true)
        assertEquals(BBox(Point3D.ORIGIN, Point3D(1.1, 2.2, 0.0)), r.boundingBox)
        assertEquals(Normal((b cross a).normalize()), r.normal)
    }

}